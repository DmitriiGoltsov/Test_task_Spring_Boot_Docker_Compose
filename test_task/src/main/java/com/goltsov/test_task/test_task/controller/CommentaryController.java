package com.goltsov.test_task.test_task.controller;

import com.goltsov.test_task.test_task.dto.CommentaryDto;
import com.goltsov.test_task.test_task.model.Commentary;
import com.goltsov.test_task.test_task.service.CommentaryService;

import com.goltsov.test_task.test_task.service.CommentaryServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.goltsov.test_task.test_task.controller.CommentaryController.COMMENTARY_CONTROLLER_URL;

@RestController
@Tag(name = "Commentary Controller")
@AllArgsConstructor
@RequestMapping(path = "${base-url}" + COMMENTARY_CONTROLLER_URL)
public class CommentaryController {

    public static final String COMMENTARY_CONTROLLER_URL = "/commentaries";
    public static final String ID = "/{id}";

    private final CommentaryServiceImplementation commentaryService;

    @Operation(summary = "Get all commentaries")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All commentaries were successfully found and loaded",
            content = @Content(schema = @Schema(implementation = Commentary.class))),
        @ApiResponse(responseCode = "404", description = "There are not commentaries stored in DB")
    })
    @GetMapping
    public List<Commentary> getAllCommentaries() {
        return commentaryService.getAllCommentary();
    }

    @Operation(summary = "Create a new commentary")
    @ApiResponse(responseCode = "201", description = "A new commentary was successfully created")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Commentary createCommentary(@RequestBody @Valid final CommentaryDto commentaryDto) {
        return commentaryService.addCommentaryToTask(commentaryDto);
    }

    @Operation(summary = "Update a particular commentary by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commentary updated successfully"),
        @ApiResponse(responseCode = "404", description = "Commentary with such id not found")
        })
    @PutMapping(ID)
    public Commentary updateCommentary(@PathVariable("id") final Long id,
                             @RequestBody final CommentaryDto commentaryDto) {

        return commentaryService.updateCommentary(id, commentaryDto);
    }

    @Operation(summary = "Get a particular commentary by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "commentary was found"),
        @ApiResponse(responseCode = "404", description = "commentary with such id does not exist")
    })
    @GetMapping(ID)
    public Commentary findCommentaryById(@PathVariable("id") final Long id) {
        return commentaryService.getCommentaryById(id);
    }

    @Operation(summary = "Delete commentary by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commentary deleted"),
        @ApiResponse(responseCode = "404", description = "Commentary with such id is not found")
    })
    @DeleteMapping(ID)
    public void deleteCommentary(@PathVariable("id") final Long id) {
        commentaryService.deleteCommentaryById(id);
    }
}
