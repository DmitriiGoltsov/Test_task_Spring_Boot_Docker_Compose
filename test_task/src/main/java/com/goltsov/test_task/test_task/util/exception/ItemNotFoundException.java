package com.goltsov.test_task.test_task.util.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String entityMessage) {
        super(entityMessage);
    }
}
