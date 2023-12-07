package com.goltsov.test_task.test_task.controller;

import com.goltsov.test_task.test_task.util.exception.ItemNotFoundException;
import com.goltsov.test_task.test_task.util.exception.ErrorMessage;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage processItemNotFoundException(ItemNotFoundException exception, WebRequest request) {
        return ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(new Date(System.currentTimeMillis()))
                .message(exception.getMessage())
                .description(request.getDescription(true))
                .build();
    }
}
