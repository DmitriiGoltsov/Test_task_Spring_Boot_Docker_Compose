package com.goltsov.test_task.test_task.service;

import com.goltsov.test_task.test_task.dto.TaskStatusDto;
import com.goltsov.test_task.test_task.model.TaskStatus;
import com.goltsov.test_task.test_task.repository.TaskStatusRepository;
import com.goltsov.test_task.test_task.util.exception.ItemNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class TaskStatusServiceImplementation implements TaskStatusService {

    private static final String EXCEPTION_MESSAGE_ID = "TaskStatus with id = ";

    private final TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus getTaskStatusById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(EXCEPTION_MESSAGE_ID + id + " not found"));
    }

    @Override
    public List<TaskStatus> getAllStatuses() {
        return taskStatusRepository.findAll();
    }

    @Override
    @Transactional
    public TaskStatus createTaskStatus(TaskStatusDto taskStatusDto) {
        return taskStatusRepository.save(new TaskStatus(taskStatusDto.getName()));
    }

    @Override
    @Transactional
    public TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDTO) {
        final TaskStatus taskStatusToUpdate = getTaskStatusById(id);

        Optional.ofNullable(taskStatusDTO.getName()).ifPresent(taskStatusToUpdate::setName);

        return taskStatusToUpdate;
    }

    @Override
    @Transactional
    public void deleteTaskStatus(Long id) {
        taskStatusRepository.delete(getTaskStatusById(id));
    }
}
