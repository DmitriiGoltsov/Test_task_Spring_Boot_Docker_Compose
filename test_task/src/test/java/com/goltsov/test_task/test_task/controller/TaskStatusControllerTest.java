package com.goltsov.test_task.test_task.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import com.goltsov.test_task.test_task.config.SpringTestConfig;
import com.goltsov.test_task.test_task.dto.TaskStatusDto;
import com.goltsov.test_task.test_task.model.TaskStatus;
import com.goltsov.test_task.test_task.repository.TaskStatusRepository;
import com.goltsov.test_task.test_task.service.TaskStatusServiceImplementation;
import com.goltsov.test_task.test_task.util.TestUtils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.goltsov.test_task.test_task.config.SpringTestConfig.TEST_PROFILE;
import static com.goltsov.test_task.test_task.controller.TaskStatusController.TASK_STATUS_URL;
import static com.goltsov.test_task.test_task.util.TestUtils.TEST_USERNAME;
import static com.goltsov.test_task.test_task.util.TestUtils.asJson;
import static com.goltsov.test_task.test_task.util.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringTestConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
public class TaskStatusControllerTest {

    private static final String BASE_URL = "/api";

    private static TaskStatusDto taskStatusDto;

    @Autowired
    private TaskStatusServiceImplementation taskStatusService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils testUtils;

    @BeforeAll
    public static void beforeAll() {
        taskStatusDto = new TaskStatusDto("Test task status");
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        testUtils.tearDown();
        testUtils.registerDefaultUser();
    }

    @AfterEach
    public void afterEach() {
        testUtils.tearDown();
    }

    @Test
    public void getAllStatusesTest() throws Exception {

        final MockHttpServletResponse response = testUtils.perform(
                        get(BASE_URL + TASK_STATUS_URL),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> statuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        final List<TaskStatus> expectedStatuses = taskStatusRepository.findAll();

        Assertions.assertThatList(statuses).isEqualTo(expectedStatuses);
    }

    @Test
    public void createNewTaskStatusTest() throws Exception {
        TaskStatus savedTaskStatus = createTaskStatusForTest(taskStatusDto);
        assertThat(taskStatusRepository.getReferenceById(savedTaskStatus.getId())).isNotNull();
    }

    @Test
    public void getTaskStatusByIdTest() throws Exception {

        final TaskStatus expectedTaskStatus = taskStatusRepository.save(TaskStatus.builder()
                .name("Task status for testing")
                .build());

        long expectedId = expectedTaskStatus.getId();

        final MockHttpServletRequestBuilder request = get(BASE_URL + TASK_STATUS_URL + "/"
                + expectedId, expectedId);

        final MockHttpServletResponse response = testUtils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus actualTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedId, actualTaskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), actualTaskStatus.getName());
    }

    @Test
    public void updateTaskStatusTest() throws Exception {

        final TaskStatus taskStatusToUpdate = createTaskStatusForTest(taskStatusDto);
        final long id = taskStatusToUpdate.getId();

        final TaskStatusDto updatedTaskStatusDto = new TaskStatusDto("Updated task status for testing");

        final MockHttpServletRequestBuilder updatingRequest = put(
                BASE_URL + TASK_STATUS_URL + "/" + id, id)
                .content(asJson(updatedTaskStatusDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(updatingRequest, TEST_USERNAME).andExpect(status().isOk());

        final TaskStatus updatedTaskStatus = taskStatusService.getTaskStatusById(id);
        assertThat(updatedTaskStatus.getName()).isEqualTo("Updated task status for testing");
    }

    @Test
    public void deleteTaskStatusTest() throws Exception {

        final TaskStatus taskStatusToDelete = createTaskStatusForTest(taskStatusDto);
        final long id = taskStatusToDelete.getId();

        testUtils.perform(delete(BASE_URL + TASK_STATUS_URL + "/" + id, id), TEST_USERNAME)
                .andExpect(status().isOk());

        assertFalse(taskStatusRepository.existsById(id));
    }

    private TaskStatus createTaskStatusForTest(final TaskStatusDto taskStatusDto) throws Exception {

        final MockHttpServletRequestBuilder request = post(BASE_URL + TASK_STATUS_URL)
                .content(asJson(taskStatusDto))
                .contentType(MediaType.APPLICATION_JSON);

        final MockHttpServletResponse response = testUtils
                .perform(request, TEST_USERNAME).andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        return fromJson(response.getContentAsString(), new TypeReference<>() { });
    }
}