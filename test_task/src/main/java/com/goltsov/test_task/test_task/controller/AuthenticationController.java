package com.goltsov.test_task.test_task.controller;

import com.goltsov.test_task.test_task.dto.LoginDto;
import com.goltsov.test_task.test_task.service.CustomUserDetailService;
import com.goltsov.test_task.test_task.util.JwtUtils;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "${base-url}")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailService userDetailsService;

    private final JwtUtils jwtUtils;

    @Operation(summary = "Process an authentication of a user")
    @PostMapping(path = "/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpServletResponse.SC_BAD_REQUEST)
                    .body("User with username " + request.getEmail() + " not found");
        }

        return ResponseEntity.ok(jwtUtils.generateToken(userDetails));
    }
}
