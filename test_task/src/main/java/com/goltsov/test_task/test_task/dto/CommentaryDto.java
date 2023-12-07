package com.goltsov.test_task.test_task.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentaryDto {

    @NotBlank(message = "Commentary has to have a content. It cannot be blank")
    private String content;

    private Long taskId;
}
