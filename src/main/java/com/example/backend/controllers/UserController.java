package com.example.backend.controllers;

import com.example.backend.exceptions.ApplicationException;
import com.example.backend.model.User;
import com.example.backend.service.IUserService;
import com.example.backend.service.JSONWebTokenService;
import com.example.backend.utils.ChangeUserRoleRequest;
import com.example.backend.utils.LoginRequest;
import com.example.backend.utils.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@Validated
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private final IUserService userService;
    private final JSONWebTokenService jwtService;

    @Autowired
    public UserController(IUserService userService,
                          JSONWebTokenService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        System.out.println("GET /user/all");
        try {
            return ResponseEntity.ok(userService.getAllUsersSimplified(token));
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.status().asHttp()).body(e.message());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody User user) {
        System.out.println("POST /user/register");
        System.out.println(user);
        return userService.tryRegister(user)
                ? ResponseEntity.ok().body("Registration successful")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username already taken");

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest) {
        System.out.println("POST /user/login");
        System.out.println(loginRequest);
        var user = userService.tryLogin(loginRequest);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
        var id = user.get().getId();
        return ResponseEntity.ok()
                .body(new LoginResponse(
                        jwtService.encode(id),
                        id,
                        userService.getPermissions(id)));

    }

    @PatchMapping("/modify")
    public ResponseEntity<?> changeUserRole(@RequestHeader("Authorization") String token,
                                            @Validated @RequestBody ChangeUserRoleRequest request) {
        System.out.println("PATCH /user/modify");
        System.out.println(request);
        try {
            userService.tryChangeUserRole(token, request);
        } catch (ApplicationException e) {
            return e.asHttpResponse();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/kick/{id}")
    public ResponseEntity<?> kickUser(@RequestHeader("Authorization") String token,
                                      @PathVariable UUID id) {
        System.out.println("DELETE /user/kick");
        System.out.println(id);
        try {
            userService.tryKickUser(token, id);
        } catch (ApplicationException e) {
            return e.asHttpResponse();
        }
        return ResponseEntity.noContent().build();
    }
}