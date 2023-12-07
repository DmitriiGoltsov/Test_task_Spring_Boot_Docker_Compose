package com.goltsov.test_task.test_task.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import com.goltsov.test_task.test_task.config.SpringTestConfig;
import com.goltsov.test_task.test_task.dto.TaskDto;
import com.goltsov.test_task.test_task.dto.TaskStatusDto;
import com.goltsov.test_task.test_task.model.Commentary;
import com.goltsov.test_task.test_task.model.Task;
import com.goltsov.test_task.test_task.model.TaskStatus;
import com.goltsov.test_task.test_task.model.User;
import com.goltsov.test_task.test_task.repository.CommentaryRepository;
import com.goltsov.test_task.test_task.repository.TaskRepository;
import com.goltsov.test_task.test_task.repository.TaskStatusRepository;
import com.goltsov.test_task.test_task.service.TaskStatusServiceImplementation;
import com.goltsov.test_task.test_task.service.UserService;
import com.goltsov.test_task.test_task.util.Priority;
import com.goltsov.test_task.test_task.util.TestUtils;

import jakarta.xml.bind.ValidationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.goltsov.test_task.test_task.config.SpringTestConfig.TEST_PROFILE;
import static com.goltsov.test_task.test_task.controller.TaskController.TASK_CONTROLLER_URL;
import static com.goltsov.test_task.test_task.util.TestUtils.TEST_USERNAME;
import static com.goltsov.test_task.test_task.util.TestUtils.asJson;
import static com.goltsov.test_task.test_task.util.TestUtils.fromJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
public class TaskControllerTest {

    private static final String BASE_URL = "/api";
    private static final int ITEMS_PER_PAGE = 5;

    private static final int NUMBER_OF_METADATA_TO_DELETE_FROM_END = 303;
    private static final int NUMBER_OF_METADATA_TO_DELETE_FROM_START = 11;

    private TaskStatusDto taskStatusDto;

    private TaskStatus taskStatus;

    private TaskDto taskDto;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentaryRepository commentaryRepository;

    @Autowired
    private TaskStatusServiceImplementation taskStatusService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @BeforeEach
    public void beforeEach() throws Exception {
        testUtils.tearDown();
        testUtils.registerDefaultUser();
        taskStatusDto = new TaskStatusDto("Test status");
        taskStatus = taskStatusService.createTaskStatus(taskStatusDto);
        taskDto = createInitialTaskDTOForTests();
    }

    @AfterEach
    public void afterEach() {
        taskRepository.deleteAll();
        commentaryRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    @Test
    public void getAllTasksTest() throws Exception {

        final List<Task> expectedTasks = IntStream.range(0, 10)
                .mapToObj(i -> Task.builder()
                        .author(testUtils.getUserByEmail(TEST_USERNAME))
                        .description("description" + i)
                        .header("someName" + i)
                        .taskStatus(taskStatus)
                        .commentaries(new HashSet<>())
                        .priority(Priority.HIGH)
                        .build())
                .toList();

        taskRepository.saveAll(expectedTasks);

        final MockHttpServletResponse response = testUtils.perform(get(
                        BASE_URL + TASK_CONTROLLER_URL), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        String responseAsString = response.getContentAsString();
        String toFormList = responseAsString.substring(NUMBER_OF_METADATA_TO_DELETE_FROM_START,
                responseAsString.length() - NUMBER_OF_METADATA_TO_DELETE_FROM_END);

        final List<Task> actualTasks = fromJson(toFormList, new TypeReference<>() { });

        assertThat(ITEMS_PER_PAGE).isEqualTo(actualTasks.size());
    }

    @Test
    public void getTaskById() throws Exception {

        final Task expected = taskRepository.save(Task.builder()
                .author(testUtils.getUserByEmail(TEST_USERNAME))
                .description("description")
                .header("name")
                .taskStatus(taskStatus)
                .build()
        );

        final Long id = expected.getId();

        final MockHttpServletRequestBuilder request = get(BASE_URL + TASK_CONTROLLER_URL + "/" + id, id);

        final MockHttpServletResponse response = testUtils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task actual = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getHeader(), actual.getHeader());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getAuthor().getId(), actual.getAuthor().getId());
    }

    @Test
    public void createTaskTest() throws Exception {
        createTaskForTest(taskDto).andExpect(status().isCreated());
        assertFalse(taskRepository.findAll().isEmpty());
    }

    @Test
    public void updateTaskTest() throws Exception {

        final MockHttpServletResponse response = createTaskForTest(taskDto)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task createdTask = fromJson(response.getContentAsString(), new TypeReference<>() { });
        Commentary commentary = new Commentary();
        commentary.setContent("Important commentary");
        createdTask.addCommentary(commentary);
        taskRepository.save(createdTask);

        Set<Long> ids = new HashSet<>();
        for (Commentary element : createdTask.getCommentaries()) {
            ids.add(element.getId());
        }

        long id = createdTask.getId();

        final TaskDto updatedTaskDTO = new TaskDto(
                "Updated task",
                "Updated description",
                createdTask.getTaskStatus().getId(),
                createdTask.getAuthor().getId(),
                createdTask.getExecutor().getId(),
                Priority.MEDIUM,
                new HashSet<>()
        );

        final MockHttpServletRequestBuilder updatingRequest = put(
                BASE_URL + TASK_CONTROLLER_URL + "/" + id, id)
                .content(asJson(updatedTaskDTO))
                .contentType(APPLICATION_JSON);

        testUtils.perform(updatingRequest, TEST_USERNAME).andExpect(status().isOk());
    }

    @Test
    public void deleteTaskTest() throws Exception {

        final Task task = taskRepository.save(Task.builder()
                .header("t name")
                .description("desc")
                .author(testUtils.getUserByEmail(TEST_USERNAME))
                .taskStatus(taskStatus)
                .priority(Priority.HIGH)
                .commentaries(new HashSet<>())
                .build());

        final long id = task.getId();

        testUtils.perform(delete(BASE_URL + TASK_CONTROLLER_URL + "/" + id, id), TEST_USERNAME)
                .andExpect(status().isOk());

        assertFalse(taskRepository.existsById(task.getId()));
    }


    private ResultActions createTaskForTest(final TaskDto taskDTO) throws Exception {
        final MockHttpServletRequestBuilder request = post(BASE_URL + TASK_CONTROLLER_URL)
                .content(asJson(taskDTO))
                .contentType(APPLICATION_JSON);

        return testUtils.perform(request, TEST_USERNAME);
    }

    private TaskDto createInitialTaskDTOForTests() throws ValidationException {

        final User user = userService.getUserByEmail(TEST_USERNAME);

        final TaskStatus taskStatus = taskStatusService.createTaskStatus(taskStatusDto);

        return new TaskDto(
                "test task",
                "test description",
                taskStatus.getId(),
                user.getId(),
                user.getId(),
                Priority.HIGH,
                new HashSet<>()
        );
    }
}