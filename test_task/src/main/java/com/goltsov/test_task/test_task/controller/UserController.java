package com.goltsov.test_task.test_task.controller;

import com.goltsov.test_task.test_task.dto.UserDto;
import com.goltsov.test_task.test_task.model.User;

import com.goltsov.test_task.test_task.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

import static com.goltsov.test_task.test_task.controller.UserController.USER_CONTROLLER_PATH;

@Tag(name = "User controller")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "${base-url}" + USER_CONTROLLER_PATH)
public class UserController {

    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private final UserService userService;

    public static final String ONLY_USER_BY_ID = """
            @userService.getUserById(#id).getEmail() == authentication.getName()
            """;

    @Operation(summary = "Operation to get all users stored in the DB")
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200", description = "Users were successfully found and loaded",
                content = @Content(schema = @Schema(implementation = User.class))
            ),
        @ApiResponse(responseCode = "404", description = "There are not stored users in the DB")
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Operation adds a new user to the DB")
    @ApiResponse(responseCode = "201", description = "User was successfully created")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@RequestBody UserDto userDTO) {
        log.info("Creating a new user");
        return userService.createUser(userDTO);
    }

    @Operation(summary = "Get a user by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User was found"),
        @ApiResponse(responseCode = "404", description = "User with such id has not been found")
    })
    @GetMapping(ID)
    public User getUser(@PathVariable("id") final Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Operation changes characteristics of the particular user that is found by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User was found and updated"),
        @ApiResponse(responseCode = "404", description = "User with such id has not been found")
    })
    @PreAuthorize(ONLY_USER_BY_ID)
    @PutMapping(ID)
    public User updateUser(@PathVariable Long id,
                           @RequestBody UserDto userDTO) {

        return userService.updateUserById(id, userDTO);
    }

    @Operation(summary = "Operation deletes the particular user with given id from the DB")
    @PreAuthorize(ONLY_USER_BY_ID)
    @DeleteMapping(ID)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
