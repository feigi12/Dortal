package com.example.Dortal.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.Dortal.insurance.controller.AuthController;
import com.example.Dortal.insurance.dto.LoginRequest;
import com.example.Dortal.insurance.entity.User;
import com.example.Dortal.insurance.security.JwtResponse;
import com.example.Dortal.insurance.security.JwtTokenProvider;
import com.example.Dortal.insurance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignup_UserAlreadyExists() {
        User user = new User();
        user.setUsername("existingUser");

        when(userService.usernameExists(user.getUsername())).thenReturn(true);

        ResponseEntity<String> response = authController.signup(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists!", response.getBody());
    }

    @Test
    public void testSignup_UserSuccessfullyRegistered() {
        User user = new User();
        user.setUsername("newUser");

        when(userService.usernameExists(user.getUsername())).thenReturn(false);

        ResponseEntity<String> response = authController.signup(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully!", response.getBody());
        verify(userService).saveUser(user);
    }

    @Test
    public void testLogin_ValidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("encodedPassword");
        User user = new User();
        user.setUsername("user");
        user.setPassword("encodedPassword");

        lenient().when(userService.loadUserByUsername(loginRequest.getUsername())).thenReturn(user);
        lenient().when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);
        lenient().when(jwtTokenProvider.renewTokenIfNeeded(user.getUsername())).thenReturn("sampleJwtToken");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("sampleJwtToken", ((JwtResponse) response.getBody()).getToken());

    }
}
