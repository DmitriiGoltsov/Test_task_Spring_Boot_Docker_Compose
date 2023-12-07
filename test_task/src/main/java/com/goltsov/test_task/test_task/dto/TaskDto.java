package com.goltsov.test_task.test_task.dto;

import com.goltsov.test_task.test_task.util.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Task status cannot be blank or null")
    private Long taskStatusId;

    private Long authorId;

    private Long executorId;

    private Priority priority;

    private Set<Long> commentaryIds;

    public TaskDto(String name, String description, Long taskStatusId,
                   Long authorId, Long executorId, Priority priority) {
        this.name = name;
        this.description = description;
        this.taskStatusId = taskStatusId;
        this.authorId = authorId;
        this.executorId = executorId;
        this.priority = priority;
    }
}
