package com.example.backend.service;

import com.example.backend.exceptions.InvalidJWTException;
import com.example.backend.exceptions.PermissionDeniedException;
import com.example.backend.model.SimplifiedUser;
import com.example.backend.model.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.utils.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    private final UserRepository source;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    private final JSONWebTokenService jwtService;
    private final UserPermissionService userPermissionService;

    @Autowired
    public UserService(UserRepository source,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roles,
                       JSONWebTokenService jwtService,
                       UserPermissionService userPermissionService) {
        this.source = source;
        this.passwordEncoder = passwordEncoder;
        this.roles = roles;
        this.jwtService = jwtService;
        this.userPermissionService = userPermissionService;
    }

    @Override
    public boolean correctPassword(UUID id, String rawPassword) {
        return source.findById(id)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    @Override
    public boolean tryRegister(User user) {
        return source.findByUsername(user.getUsername())
                .map(existing -> false)
                .orElseGet(() -> {
                    user.setRole(roles.getByName("user"));
                    save(user);
                    return true;
                });
    }

    @Override
    public Optional<User> tryLogin(LoginRequest request) {
        return source.findByUsername(request.username())
                .filter(dbUserRow -> correctPassword(dbUserRow.getId(), request.password()));
    }

    @Override
    public Collection<User> getAll() {
        return source.findAll();
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
    public List<String> getPermissions(UUID userId) {
        return source.findPermissionsByUserId(userId);
    }

    @Override
    public List<SimplifiedUser> getAllUsersSimplified(String token) throws InvalidJWTException, PermissionDeniedException {
        if (token == null || jwtService.hasExpired(token)) {
            throw new InvalidJWTException();
        }
        UUID id = jwtService.parse(token);
        if (!userPermissionService.canAssign(id)) {
            throw new PermissionDeniedException();
        }
        var roleIDToName = this.roles.findAll().stream()
                .map(role -> new AbstractMap.SimpleImmutableEntry<>(role.getId(), role.getName()))
                .collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue));
        return getAll().stream()
                .map(user -> new SimplifiedUser(user.getId(),
                        user.getUsername(),
                        roleIDToName.get(user.getRole())))
                .toList();
    }
}
