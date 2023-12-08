package com.goltsov.test_task.test_task.controller;

import com.goltsov.test_task.test_task.util.exception.ItemNotFoundException;
import com.goltsov.test_task.test_task.util.exception.ErrorMessage;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Process exceptions coming from trying to get an item that does not exit in DB. "
            + "Form correct and informative exception message")
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
