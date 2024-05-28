package com.example.backend.service;

import com.example.backend.exceptions.HttpTokenException;
import com.example.backend.model.Role;
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

import static com.example.backend.exceptions.FailureReason.JWT_EXPIRED;
import static com.example.backend.exceptions.FailureReason.PERMISSION_DENIED;

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
    public void save(User entity) {
        User newUser = new User();
        newUser.setId(entity.getId());
        newUser.setUsername(entity.getUsername());
        newUser.setPassword(passwordEncoder.encode(entity.getPassword()));
        newUser.setEmail(entity.getEmail());
        source.save(newUser);
    }

    @Override
    public List<String> getPermissions(UUID userId) {
        return source.findPermissionsByUserId(userId);
    }

    @Override
    public List<SimplifiedUser> getAllUsersSimplified(String token) throws HttpTokenException {
        if (token == null || jwtService.hasExpired(token)) {
            throw new HttpTokenException(JWT_EXPIRED);
        }
        UUID id = jwtService.parse(token);
        if (!userPermissionService.canAssign(id)) {
            throw new HttpTokenException(PERMISSION_DENIED);
        }
        Map<UUID, String> roleIDToName = this.roles
                .findAll()
                .stream()
                .collect(Collectors.toMap(
                        Role::getId,
                        Role::getName));
        return getAll().stream()
                .map(user -> new SimplifiedUser(
                        user.getId(),
                        user.getUsername(),
                        roleIDToName.get(user.getRole())))
                .toList();
    }
}
