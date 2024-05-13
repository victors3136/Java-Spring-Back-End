package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JSONWebTokenGenerationService;
import com.example.backend.user_requests.ChangePasswordRequest;
import com.example.backend.user_requests.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Validated
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JSONWebTokenGenerationService jsonWebTokenGenerationService;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, JSONWebTokenGenerationService jsonWebTokenGenerationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jsonWebTokenGenerationService = jsonWebTokenGenerationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody User user) {
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().body("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Validated @RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.username());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        String jsonWebToken = jsonWebTokenGenerationService.generateToken(user.getUsername());
        return ResponseEntity.ok().body(jsonWebToken);
    }

    @PutMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        String username = changePasswordRequest.username();
        String oldPassword = changePasswordRequest.oldPassword();
        String newPassword = changePasswordRequest.newPassword();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        User user = optionalUser.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok().body("Password changed successfully");
    }
}

