package com.goltsov.test_task.test_task.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import com.goltsov.test_task.test_task.config.SpringTestConfig;
import com.goltsov.test_task.test_task.dto.CommentaryDto;
import com.goltsov.test_task.test_task.model.Commentary;
import com.goltsov.test_task.test_task.model.Task;
import com.goltsov.test_task.test_task.model.TaskStatus;
import com.goltsov.test_task.test_task.repository.CommentaryRepository;
import com.goltsov.test_task.test_task.repository.TaskRepository;
import com.goltsov.test_task.test_task.repository.TaskStatusRepository;
import com.goltsov.test_task.test_task.repository.UserRepository;
import com.goltsov.test_task.test_task.service.CommentaryServiceImplementation;
import com.goltsov.test_task.test_task.util.Priority;
import com.goltsov.test_task.test_task.util.TestUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static com.goltsov.test_task.test_task.config.SpringTestConfig.TEST_PROFILE;
import static com.goltsov.test_task.test_task.controller.CommentaryController.COMMENTARY_CONTROLLER_URL;
import static com.goltsov.test_task.test_task.util.TestUtils.TEST_USERNAME;
import static com.goltsov.test_task.test_task.util.TestUtils.asJson;
import static com.goltsov.test_task.test_task.util.TestUtils.fromJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringTestConfig.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(TEST_PROFILE)
public class CommentaryControllerTest {

    private static final String BASE_URL = "/api";

    private Task task;

    private static CommentaryDto commentaryDto;

    @Autowired
    private CommentaryRepository commentaryRepository;

    @Autowired
    private CommentaryServiceImplementation commentaryService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() throws Exception {
        commentaryDto = new CommentaryDto();
        commentaryDto.setContent("Test commentary");

        testUtils.tearDown();
        testUtils.registerDefaultUser();

        TaskStatus testTaskStatus = taskStatusRepository.save(new TaskStatus("Test status"));

        task = taskRepository.save(new Task(
                "Test task",
                "Desc",
                testTaskStatus,
                userRepository.findUserByEmail(TEST_USERNAME).orElse(null),
                userRepository.findUserByEmail(TEST_USERNAME).orElse(null),
                new HashSet<>(),
                Priority.HIGH
        ));

        commentaryDto.setTaskId(task.getId());
    }

    @AfterEach
    public void afterEach() {
        commentaryRepository.deleteAll();
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    @Test
    public void getAllCommentariesTest() throws Exception {

        final List<Commentary> expectedLabels = IntStream.range(0, 10)
                .mapToObj(i -> Commentary.builder()
                        .content("commentary number " + i)
                        .task(task)
                        .build())
                .toList();

        commentaryRepository.saveAll(expectedLabels);

        final MockHttpServletResponse response = testUtils.perform(get(
                        BASE_URL + COMMENTARY_CONTROLLER_URL), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Commentary> actualLabels = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(actualLabels).hasSize(expectedLabels.size());
    }

    @Test
    public void getCommentaryByIdTest() throws Exception {

        final Commentary expectedCommentary = commentaryRepository.save(Commentary.builder()
                .content("Test label")
                .task(task)
                .build());

        final long id = expectedCommentary.getId();
        final MockHttpServletRequestBuilder request = get(BASE_URL
                + COMMENTARY_CONTROLLER_URL + "/" + id, id);
        final MockHttpServletResponse response = testUtils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Commentary actualCommentary = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedCommentary.getId(), actualCommentary.getId());
        assertEquals(expectedCommentary.getContent(), actualCommentary.getContent());
    }

    @Test
    public void createCommentaryTest() throws Exception {
        createCommentaryForTest(commentaryDto);
        assertFalse(commentaryRepository.findAll().isEmpty());
    }

    @Test
    public void updateCommentaryTest() throws Exception {

        final Commentary createdCommentary = createCommentaryForTest(commentaryDto);
        final long id = createdCommentary.getId();

        final CommentaryDto updatedLabelDto = new CommentaryDto();
        updatedLabelDto.setContent("Updated name of LabelDTO");

        final MockHttpServletRequestBuilder updatingRequest = put(
                BASE_URL + COMMENTARY_CONTROLLER_URL + "/" + id, id)
                .content(asJson(updatedLabelDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(updatingRequest, TEST_USERNAME).andExpect(status().isOk());
        final Commentary updatedLabel = commentaryService.getCommentaryById(id);
        assertThat(updatedLabel.getContent()).isEqualTo("Updated name of LabelDTO");
    }

    @Test
    public void deleteCommentaryTest() throws Exception {

        final Commentary createdLabel = createCommentaryForTest(commentaryDto);
        final long id = createdLabel.getId();

        testUtils.perform(delete(BASE_URL + COMMENTARY_CONTROLLER_URL + "/" + id, id), TEST_USERNAME)
                .andExpect(status().isOk());

        assertFalse(commentaryRepository.existsById(id));
    }

    private Commentary createCommentaryForTest(final CommentaryDto commentaryDto) throws Exception {

        final MockHttpServletRequestBuilder request = post(BASE_URL + COMMENTARY_CONTROLLER_URL)
                .content(asJson(commentaryDto))
                .contentType(MediaType.APPLICATION_JSON);

        System.out.println("Task Repository Content: " + taskRepository.findAll());


        final MockHttpServletResponse response = testUtils
                .perform(request, TEST_USERNAME).andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        return fromJson(response.getContentAsString(), new TypeReference<>() { });
    }
}
