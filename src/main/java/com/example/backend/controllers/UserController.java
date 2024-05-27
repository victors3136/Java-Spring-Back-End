package com.example.backend.controllers;

import com.example.backend.model.User;
import com.example.backend.service.IUserService;
import com.example.backend.service.JSONWebTokenService;
import com.example.backend.utils.ChangePasswordRequest;
import com.example.backend.utils.LoginRequest;
import com.example.backend.utils.LoginResponse;
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
    private final IUserService userService;
    private final JSONWebTokenService jwtService;

    @Autowired
    public UserController(IUserService userService, JSONWebTokenService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody User user) {
        System.out.println("POST /user/register");
        System.out.println(user);
        try {
            return userService.tryRegister(user)
                    ? ResponseEntity.ok().body("Registration successful")
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username already taken");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest) {
        System.out.println("POST /user/login");
        System.out.println(loginRequest);
        try {
            var user = userService.tryLogin(loginRequest);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username or password");
            }
            var id = user.get().getId();
            return ResponseEntity.ok()
                    .body(new LoginResponse(
                            jwtService.encode(id),
                            userService.getPermissions(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PatchMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        System.out.println("PATCH user/change_password");
        System.out.println(changePasswordRequest);
        try {
            return userService.tryChangePassword(changePasswordRequest)
                    ? ResponseEntity.ok().body("Password changed successfully")
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}