package com.goltsov.test_task.test_task.service;

import com.goltsov.test_task.test_task.dto.TaskDto;
import com.goltsov.test_task.test_task.model.Task;

import com.querydsl.core.types.Predicate;

public interface TaskService {

    Task createNewTask(TaskDto taskDto);

    Iterable<Task> getAllTasksByCriteria(Predicate predicate, int pageNumber);

    Task getTaskById(Long id);

    Task updateTask(Long id, TaskDto taskDto);

    void deleteTaskById(Long id);
}
