package com.example.backend.controllers;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.requests.ChangePasswordRequest;
import com.example.backend.requests.LoginRequest;
import com.example.backend.service.JSONWebTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JSONWebTokenGeneratorService jwtTokenGeneratorService;

    @Autowired
    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JSONWebTokenGeneratorService jwtTokenGeneratorService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenGeneratorService = jwtTokenGeneratorService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody User user) {
        System.out.println("POST /user/register");
        System.out.println(user);
        List<User> users = userRepository.findAll().stream().filter(u -> Objects.equals(u.getUsername(), user.getUsername())).toList();
        if (!users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().body("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Validated @RequestBody LoginRequest loginRequest) {
        System.out.println("POST /user/login");
        System.out.println(loginRequest);
        List<User> users = userRepository.findAll().stream().filter(u -> u.getUsername().equals(loginRequest.username())).toList();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or oldPassword");
        }
        User user = users.getFirst();
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or oldPassword");
        }
        String jsonWebToken = jwtTokenGeneratorService.encode(user.getId());
        return ResponseEntity.ok().body(jsonWebToken);
    }

    @PatchMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        System.out.println("PATCH user/change_password");
        System.out.println(changePasswordRequest);

        String username = changePasswordRequest.username(),
                oldPassword = changePasswordRequest.oldPassword(),
                newPassword = changePasswordRequest.newPassword();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or oldPassword");
        }
        User user = optionalUser.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or oldPassword");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok().body("Password changed successfully");
    }
}