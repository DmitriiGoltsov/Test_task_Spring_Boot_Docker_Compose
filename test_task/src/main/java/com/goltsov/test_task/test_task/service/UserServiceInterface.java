package com.goltsov.test_task.test_task.service;

import com.goltsov.test_task.test_task.dto.UserDto;
import com.goltsov.test_task.test_task.model.User;

import java.util.List;

public interface UserServiceInterface {
    User createUser(UserDto userDto);

    User getUserById(Long id);

    User updateUserById(Long id, UserDto userDto);

    void deleteUserById(Long id);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    String getEmailOfCurrentUser();

    User getCurrentUser();

    User formUserFromUserDTO(UserDto userDto, User user);
}
