package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.utils.ChangePasswordRequest;
import com.example.backend.utils.LoginRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserService extends EntityService<User> {
    @SuppressWarnings("unused")
    Collection<User> findByEmail(String email);

    @SuppressWarnings("unused")
    Optional<User> findByUsername(String username);

    @SuppressWarnings("unused")
    boolean existsByUsername(String username);

    @SuppressWarnings("unused")
    boolean existsByEmail(String email);

    boolean correctPassword(UUID id, String rawPassword);

    boolean tryRegister(User user);

    Optional<User> tryLogin(LoginRequest request);

    boolean tryChangePassword(ChangePasswordRequest request);

    List<String> getPermissions(UUID userId);
}
