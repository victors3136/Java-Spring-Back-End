package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.requests.ChangePasswordRequest;
import com.example.backend.requests.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements IUserService {
    private final UserRepository source;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository source, PasswordEncoder passwordEncoder) {
        this.source = source;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Collection<User> findByEmail(String email) {
        return source.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return source.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return source.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return source.existsByEmail(email);
    }

    @Override
    public boolean correctPassword(UUID id, String rawPassword) {
        return source.findById(id)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    @Override
    public boolean tryToRegister(User user) {
        return source.findByUsername(user.getUsername())
                .map(existing -> false)
                .orElseGet(() -> {
                    this.save(user);
                    return true;
                });
    }

    @Override
    public Optional<User> tryToLogIn(LoginRequest request) {
        return source.findByUsername(request.username())
                .filter(dbUserRow -> correctPassword(dbUserRow.getId(), request.password()));
    }

    @Override
    public Collection<User> getAll() {
        return source.findAll();
    }

    @Override
    public Optional<User> getById(UUID id) {
        return source.findById(id);
    }

    @Override
    public User save(User entity) {
        User newUser = new User();
        newUser.setId(entity.getId());
        newUser.setUsername(entity.getUsername());
        newUser.setPassword(passwordEncoder.encode(entity.getPassword()));
        newUser.setEmail(entity.getEmail());
        return source.save(newUser);
    }

    @Override
    public Optional<User> update(User entity) {
        source.updateUserById(entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                passwordEncoder.encode(entity.getPassword()));
        return source.findById(entity.getId());
    }

    @Override
    public User delete(User entity) {
        source.deleteById(entity.getId());
        return entity;
    }

    @Override
    public boolean tryToChangePassword(ChangePasswordRequest request) {
        return source.findByUsername(request.username())
                .filter(user -> correctPassword(user.getId(), request.oldPassword()))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(request.newPassword()));
                    source.save(user);
                    return true;
                })
                .orElse(false);
    }
}
