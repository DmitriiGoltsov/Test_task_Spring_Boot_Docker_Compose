package com.goltsov.test_task.test_task.service;

import com.goltsov.test_task.test_task.dto.UserDto;
import com.goltsov.test_task.test_task.model.User;
import com.goltsov.test_task.test_task.repository.UserRepository;
import com.goltsov.test_task.test_task.util.exception.ItemNotFoundException;

import jakarta.persistence.EntityExistsException;

import lombok.AllArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService implements UserServiceInterface {

    private static final String EXCEPTION_MESSAGE_ID = "User with id = ";

    private static final String EXCEPTION_MESSAGE_EMAIL = "User with email ";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        if (userRepository.findUserByEmail(userDto.getEmail()).isPresent()) {
            throw new EntityExistsException(EXCEPTION_MESSAGE_EMAIL + userDto.getEmail());
        }

        User user = formUserFromUserDTO(userDto, new User());

        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(EXCEPTION_MESSAGE_ID + id + " not found"));
    }

    @Override
    @Transactional
    public User updateUserById(Long id, UserDto userDto) {
        User updatedUser = formUserFromUserDTO(userDto, getUserById(id));

        return userRepository.save(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(EXCEPTION_MESSAGE_ID + id + " not found"));

        userRepository.delete(userToDelete);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ItemNotFoundException(EXCEPTION_MESSAGE_EMAIL + email + " not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    @Override
    public String getEmailOfCurrentUser() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    @Override
    public User getCurrentUser() {
        String userEmail = getEmailOfCurrentUser();

        return getUserByEmail(userEmail);
    }

    @Override
    public User formUserFromUserDTO(UserDto userDto, User user) {
        Optional.ofNullable(userDto.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(userDto.getSurname()).ifPresent(user::setSurname);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        return user;
    }
}
