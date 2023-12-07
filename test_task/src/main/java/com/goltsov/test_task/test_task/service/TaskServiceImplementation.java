package com.goltsov.test_task.test_task.service;

import com.goltsov.test_task.test_task.dto.TaskDto;
import com.goltsov.test_task.test_task.model.Commentary;
import com.goltsov.test_task.test_task.model.Task;
import com.goltsov.test_task.test_task.model.TaskStatus;
import com.goltsov.test_task.test_task.model.User;
import com.goltsov.test_task.test_task.repository.CommentaryRepository;
import com.goltsov.test_task.test_task.repository.TaskRepository;
import com.goltsov.test_task.test_task.util.Priority;
import com.goltsov.test_task.test_task.util.exception.ItemNotFoundException;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.querydsl.core.types.Predicate;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class TaskServiceImplementation implements TaskService {

    private static final String EXCEPTION_MESSAGE_ID = "Task with id = ";

    private static final int ITEMS_PER_PAGE = 5;

    private final UserService userService;

    private final TaskRepository taskRepository;

    private final TaskStatusServiceImplementation taskStatusService;

    private final CommentaryRepository commentaryRepository;

    @Override
    @Transactional
    public Task createNewTask(TaskDto taskDto) {
        Task newTask = formTaskFromDto(taskDto, new Task());

        return taskRepository.save(newTask);
    }

    @Override
    public Iterable<Task> getAllTasksByCriteria(Predicate predicate, int pageNumber) {
        return taskRepository.findAll(predicate, PageRequest.of(pageNumber, ITEMS_PER_PAGE));
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(EXCEPTION_MESSAGE_ID + id + " not found"));
    }

    @Override
    @Transactional
    public Task updateTask(Long id, TaskDto taskDto) {
        return taskRepository.save(formTaskFromDto(taskDto, getTaskById(id)));
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        taskRepository.delete(getTaskById(id));
    }

    private Task formTaskFromDto(TaskDto taskDto, Task task) {

        final User author = userService.getCurrentUser();
        final TaskStatus taskStatus = taskStatusService.getTaskStatusById(taskDto.getTaskStatusId());
        final String taskHeader = taskDto.getName();
        final Set<Commentary> commentaries = Optional.ofNullable(taskDto.getCommentaryIds())
                .map(commentaryIds -> new HashSet<>(commentaryRepository.findAllById(commentaryIds)))
                .orElseGet(HashSet::new);
        final Optional<Priority> priorityOptional = Optional.ofNullable(taskDto.getPriority());

        task.setAuthor(author);
        task.setHeader(taskHeader);
        task.setTaskStatus(taskStatus);
        commentaries.forEach(task::addCommentary);
        priorityOptional.ifPresent(task::setPriority);

        Optional.ofNullable(taskDto.getDescription())
                .ifPresentOrElse(task::setDescription, () -> task.setDescription(""));

        if (Optional.ofNullable(taskDto.getExecutorId()).isPresent()) {
            task.setExecutor(userService.getUserById(taskDto.getExecutorId()));
        }

        return task;
    }
}
