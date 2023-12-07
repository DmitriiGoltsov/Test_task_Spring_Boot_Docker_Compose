package com.goltsov.test_task.test_task.util.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
@Builder
public class ErrorMessage {

    private int statusCode;

    private Date timestamp;

    private String message;

    private String description;

}
