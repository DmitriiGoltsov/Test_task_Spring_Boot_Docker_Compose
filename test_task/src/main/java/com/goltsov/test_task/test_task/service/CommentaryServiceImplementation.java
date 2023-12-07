package com.goltsov.test_task.test_task.service;

import com.goltsov.test_task.test_task.dto.CommentaryDto;
import com.goltsov.test_task.test_task.model.Commentary;
import com.goltsov.test_task.test_task.model.Task;
import com.goltsov.test_task.test_task.repository.CommentaryRepository;
import com.goltsov.test_task.test_task.util.exception.ItemNotFoundException;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CommentaryServiceImplementation implements CommentaryService {

    private static final String EXCEPTION_MESSAGE = "Commentary with id = ";

    private final CommentaryRepository commentaryRepository;

    private final TaskServiceImplementation taskService;

    private final UserService userService;

    @Override
    public List<Commentary> getAllCommentary() {
        return commentaryRepository.findAll();
    }

    @Override
    @Transactional
    public Commentary addCommentaryToTask(CommentaryDto commentaryDto) {
        Task task = taskService.getTaskById(commentaryDto.getTaskId());
        Commentary commentary = formCommentary(task, commentaryDto);

        commentaryRepository.save(commentary);
        return commentary;
    }

    @Override
    public Commentary getCommentaryById(Long id) {
        return commentaryRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(EXCEPTION_MESSAGE + id + " not found"));
    }

    @Override
    @Transactional
    public Commentary updateCommentary(Long id, CommentaryDto commentaryDto) {
        Commentary commentaryToUpdate = getCommentaryById(id);

        Optional.ofNullable(commentaryDto.getContent()).ifPresent(commentaryToUpdate::setContent);
        if (commentaryDto.getTaskId() != null) {
            commentaryToUpdate.setTask(taskService.getTaskById(commentaryDto.getTaskId()));
        }

        return commentaryRepository.save(commentaryToUpdate);
    }

    @Override
    @Transactional
    public void deleteCommentaryById(Long id) {
        Commentary commentary = commentaryRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Commentary with id " + id + " not found"));

        Long taskId = Optional.ofNullable(commentary.getTask().getId())
                .orElseThrow(() -> new ItemNotFoundException("There is no taskId in the comment with id " + id));

        Task task = taskService.getTaskById(taskId);

        task.removeCommentary(commentary);
        commentaryRepository.deleteById(id);
    }

    private Commentary formCommentary(Task task, CommentaryDto commentaryDto) {
        Commentary result = new Commentary();
        result.setAuthor(userService.getCurrentUser());
        result.setContent(commentaryDto.getContent());

        task.addCommentary(result);
        result.setTask(task);

        return result;
    }
}
