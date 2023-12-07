package com.goltsov.test_task.test_task.service;

import com.goltsov.test_task.test_task.dto.TaskStatusDto;
import com.goltsov.test_task.test_task.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {

    TaskStatus getTaskStatusById(Long id);

    List<TaskStatus> getAllStatuses();

    TaskStatus createTaskStatus(TaskStatusDto taskStatusDTO);

    TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDTO);

    void deleteTaskStatus(Long id);

}
