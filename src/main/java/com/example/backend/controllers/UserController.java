package com.example.backend.controllers;

import com.example.backend.model.User;
import com.example.backend.requests.ChangePasswordRequest;
import com.example.backend.requests.LoginRequest;
import com.example.backend.service.JSONWebTokenGeneratorService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final JSONWebTokenGeneratorService jwtTokenGeneratorService;

    @Autowired
    public UserController(UserService userService, JSONWebTokenGeneratorService jwtTokenGeneratorService) {
        this.jwtTokenGeneratorService = jwtTokenGeneratorService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody User user) {
        System.out.println("POST /user/register");
        System.out.println(user);
        try {
            return userService.tryToRegister(user)
                    ? ResponseEntity.ok().body("Registration successful")
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username already taken");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Validated @RequestBody LoginRequest loginRequest) {
        System.out.println("POST /user/login");
        System.out.println(loginRequest);
        try {
            return userService.tryToLogIn(loginRequest)
                    .map(user -> ResponseEntity.ok()
                            .body(jwtTokenGeneratorService.encode(user.getId())))
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PatchMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        System.out.println("PATCH user/change_password");
        System.out.println(changePasswordRequest);
        try {
            return userService.tryToChangePassword(changePasswordRequest)
                    ? ResponseEntity.ok().body("Password changed successfully")
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}