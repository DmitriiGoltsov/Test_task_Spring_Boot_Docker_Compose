package com.goltsov.test_task.test_task.service;

import com.goltsov.test_task.test_task.dto.CommentaryDto;
import com.goltsov.test_task.test_task.model.Commentary;

import java.util.List;

public interface CommentaryService {

    List<Commentary> getAllCommentary();

    Commentary addCommentaryToTask(CommentaryDto commentaryDto);

    Commentary getCommentaryById(Long id);

    Commentary updateCommentary(Long id, CommentaryDto commentaryDto);

    void deleteCommentaryById(Long id);

}
