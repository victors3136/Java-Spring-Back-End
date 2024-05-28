package com.example.backend.service;

import com.example.backend.exceptions.InvalidJWTException;
import com.example.backend.exceptions.PermissionDeniedException;
import com.example.backend.model.SimplifiedUser;
import com.example.backend.model.User;
import com.example.backend.utils.LoginRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserService extends EntityService<User> {

    boolean correctPassword(UUID id, String rawPassword);

    boolean tryRegister(User user);

    Optional<User> tryLogin(LoginRequest request);

    List<String> getPermissions(UUID userId);

    List<SimplifiedUser> getAllUsersSimplified(String token) throws InvalidJWTException, PermissionDeniedException;
}
