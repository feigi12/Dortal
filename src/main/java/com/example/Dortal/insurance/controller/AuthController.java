package com.example.Dortal.insurance.controller;

import com.example.Dortal.insurance.dto.LoginRequest;
import com.example.Dortal.insurance.entity.User;
import com.example.Dortal.insurance.security.JwtResponse;
import com.example.Dortal.insurance.security.JwtTokenProvider;
import com.example.Dortal.insurance.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        if (userService.usernameExists(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = (User) userService.loadUserByUsername(loginRequest.getUsername());
        if (user == null || passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        String jwt = jwtTokenProvider.renewTokenIfNeeded(user.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

}
