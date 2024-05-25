package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.requests.ChangePasswordRequest;
import com.example.backend.requests.LoginRequest;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface IUserService extends EntityService<User> {
    Collection<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean correctPassword(UUID id, String rawPassword);

    boolean tryToRegister(User user);

    Optional<User> tryToLogIn(LoginRequest request);

    boolean tryToChangePassword(ChangePasswordRequest request);
}
