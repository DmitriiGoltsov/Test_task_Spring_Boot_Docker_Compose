package com.goltsov.test_task.test_task.controller;

import com.goltsov.test_task.test_task.dto.TaskDto;
import com.goltsov.test_task.test_task.model.Task;
import com.goltsov.test_task.test_task.service.TaskService;
import com.goltsov.test_task.test_task.service.TaskServiceImplementation;
import com.querydsl.core.types.Predicate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static com.goltsov.test_task.test_task.controller.TaskController.TASK_CONTROLLER_URL;

@Tag(name = "Task controller")
@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_URL)
@RequiredArgsConstructor
public class TaskController {

    public static final String TASK_CONTROLLER_URL = "/tasks";
    public static final String ID = "/{id}";
    private static final String ONLY_OWNER_BY_ID =
            "@taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()";

    private final TaskServiceImplementation taskService;

    @Operation(description = "Get all tasks of all authors")
    @ApiResponse(responseCode = "200", description = "5 tasks on a requested page are loaded")
    @GetMapping
    public Iterable<Task> getAllTasksByCriteria(@QuerydslPredicate(root = Task.class) final Predicate predicate,
                                                @PathParam("pageNumber") final Integer pageNumber) {
        return taskService.getAllTasksByCriteria(predicate, Objects.requireNonNullElse(pageNumber, 0));
    }

    @Operation(description = "Get a particular task by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The task was found and loaded"),
        @ApiResponse(responseCode = "404", description = "Task with such id does not exist")
    })
    @GetMapping(ID)
    public Task getTaskById(@PathVariable("id") @Valid final Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(description = "Create a new task")
    @ApiResponse(responseCode = "201", description = "Task was successfully created")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Task createTask(
            @Parameter(schema = @Schema(implementation = TaskDto.class))
            @RequestBody final TaskDto taskDTO) {
        return taskService.createNewTask(taskDTO);
    }

    @Operation(description = "Update task by uts id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated"),
        @ApiResponse(responseCode = "404", description = "Task with this ID not found")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @PutMapping(ID)
    public Task updateTask(@PathVariable("id") @Valid final Long id,
                           @RequestBody @Valid final TaskDto taskDTO) {

        return taskService.updateTask(id, taskDTO);
    }

    @Operation(description = "Delete task by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task was deleted"),
        @ApiResponse(responseCode = "404", description = "Task with such id is not found")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @DeleteMapping(ID)
    public void deleteTask(@PathVariable("id") @Valid final Long id) {
        taskService.deleteTaskById(id);
    }
}
