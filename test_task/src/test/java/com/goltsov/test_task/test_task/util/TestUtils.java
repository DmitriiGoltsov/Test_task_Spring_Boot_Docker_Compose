package com.goltsov.test_task.test_task.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.goltsov.test_task.test_task.dto.UserDto;
import com.goltsov.test_task.test_task.model.User;
import com.goltsov.test_task.test_task.repository.TaskStatusRepository;
import com.goltsov.test_task.test_task.repository.UserRepository;
import com.goltsov.test_task.test_task.service.CustomUserDetailService;
import com.goltsov.test_task.test_task.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.goltsov.test_task.test_task.controller.UserController.USER_CONTROLLER_PATH;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    private static final String BEARER = "Bearer ";
    private static final String BASE_URL = "/api";
    public static final String TEST_USERNAME = "email@email.com";
    public static final String NEW_USERNAME = "newmail@gmail.com";

    private final UserDto testRegistrationDto = new UserDto(
            "fname",
            "lname",
            TEST_USERNAME,
            "pwd"
    );

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserService userService;

    public void tearDown() {
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

    public ResultActions registerUser(final UserDto userDTO) throws Exception {
        final MockHttpServletRequestBuilder request = post(BASE_URL + USER_CONTROLLER_PATH)
                .content(asJson(userDTO))
                .contentType(MediaType.APPLICATION_JSON);
        return perform(request);
    }

    public ResultActions registerDefaultUser() throws Exception {
        return registerUser(getTestRegistrationDto());
    }

    public String buildToken(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return jwtUtils.generateToken(userDetails);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String email = userRepository.findUserByEmail(byUser)
                .map(User::getEmail)
                .orElse(null);

        final String token = buildToken(email);
        return performWithToken(request, token);
    }

    public ResultActions performWithToken(final MockHttpServletRequestBuilder request,
                                          final String token) throws Exception {
        request.header(AUTHORIZATION, BEARER + token);
        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public User getUserByEmail(final String email) {
        return userService.getUserByEmail(email);
    }
}