package com.goltsov.test_task.test_task.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusDto {

    @NotBlank(message = "Task status name cannot be blank")
    private String name;

}
