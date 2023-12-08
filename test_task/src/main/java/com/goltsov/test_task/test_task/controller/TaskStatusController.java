package com.goltsov.test_task.test_task.controller;

import com.goltsov.test_task.test_task.dto.TaskStatusDto;
import com.goltsov.test_task.test_task.model.TaskStatus;
import com.goltsov.test_task.test_task.service.TaskStatusServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.goltsov.test_task.test_task.controller.TaskStatusController.TASK_STATUS_URL;

@RestController
@Slf4j
@Tag(name = "Task statuses controller")
@RequestMapping("${base-url}" + TASK_STATUS_URL)
@AllArgsConstructor
public class TaskStatusController {

    public static final String TASK_STATUS_URL = "/statuses";

    public static final String ID = "/{id}";

    private final TaskStatusServiceImplementation taskStatusService;

    @Operation(description = "Get all statuses of all tasks")
    @ApiResponse(responseCode = "200", description = "All task statuses are loaded",
            content = @Content(schema = @Schema(implementation = TaskStatus.class)))
    @GetMapping
    public List<TaskStatus> getAllStatuses() {
        return taskStatusService.getAllStatuses();
    }

    @Operation(description = "Create new task status")
    @ApiResponse(responseCode = "201", description = "Task status was successfully created")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public TaskStatus createTaskStatus(@RequestBody @Valid final TaskStatusDto dto) {
        return taskStatusService.createTaskStatus(dto);
    }

    @Operation(description = "Get task status by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status was successfully loaded"),
        @ApiResponse(responseCode = "404", description = "Task status was not found")
    })
    @GetMapping(ID)
    public TaskStatus getStatusById(@PathVariable("id") final Long id) {
        return taskStatusService.getTaskStatusById(id);
    }

    @Operation(description = "Update user by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status was successfully updated"),
        @ApiResponse(responseCode = "404", description = "Task status was not found")
    })
    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@PathVariable("id") final Long id,
                                       @RequestBody @Valid final TaskStatusDto dto) {
        return taskStatusService.updateTaskStatus(id, dto);
    }

    @Operation(description = "Delete a task status by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status was deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task status was not found")
    })
    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable("id") final Long id) {
        taskStatusService.deleteTaskStatus(id);
    }
}
