package com.example.backend.service;

import com.example.backend.exceptions.InvalidJWTException;
import com.example.backend.exceptions.NotFoundException;
import com.example.backend.exceptions.PermissionDeniedException;
import com.example.backend.exceptions.ValidationException;
import com.example.backend.model.Subtask;
import com.example.backend.repository.SubtaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubtaskService implements ISubtaskService {
    private final SubtaskRepository source;
    private final JSONWebTokenService jwtService;
    private final UserPermissionService userPermissionService;

    public SubtaskService(SubtaskRepository source, JSONWebTokenService jwtService, UserPermissionService userPermissionService) {
        this.source = source;
        this.jwtService = jwtService;
        this.userPermissionService = userPermissionService;
    }

    @Override
    public Collection<Subtask> getAll(String token) {
        return source.findAll();
    }

    @Override
    public Optional<Subtask> getById(UUID id, String token) {
        return source.findById(id);
    }

    @Override
    public Subtask save(Subtask entity, String token) throws ValidationException {
        if (entity.validationFails()) {
            throw new ValidationException();
        }
        Subtask newSubtask = new Subtask();
        newSubtask.setSubject(entity.getSubject());
        newSubtask.setTask(entity.getTask());
        return source.save(newSubtask);
    }

    @Override
    public Optional<Page<Subtask>> getPage(int pageNumber,
                                           int pageSize,
                                           UUID taskID,
                                           String token) {
        if (pageNumber < 0 || pageSize < 0 || taskID == null) {
            return Optional.empty();
        }
        return Optional.of(
                source.findAll(
                        (subtask, query, where) -> where
                                .equal(subtask.get("task"),
                                        taskID),
                        PageRequest.of(pageNumber, pageSize)));
    }

    @Override
    public long countSubtasksByTask(UUID id, String token) throws InvalidJWTException, PermissionDeniedException {
        if (jwtService.hasExpired(token)) {
            throw new InvalidJWTException();
        }
        if (!userPermissionService.canRead(jwtService.parse(token))) {
            throw new PermissionDeniedException();
        }
        return source.countSubtasksByTask(id);
    }

    @Override
    public Subtask tryToUpdate(UUID id, Subtask subtask, String token) throws InvalidJWTException, PermissionDeniedException, NotFoundException, ValidationException {
        if (jwtService.hasExpired(token)) {
            throw new InvalidJWTException();
        }
        if (!userPermissionService.canUpdate(jwtService.parse(token), subtask)) {
            throw new PermissionDeniedException();
        }
        if (subtask.validationFails()) {
            throw new ValidationException();
        }
        var maybeSubtask = source.findById(id);
        if (maybeSubtask.isEmpty()) {
            throw new NotFoundException();
        }
        var toUpdate = maybeSubtask.get();
        Subtask updated = new Subtask();
        updated.setId(toUpdate.getId());
        updated.setSubject(subtask.getSubject());
        updated.setTask(subtask.getTask());
        return source.save(updated);
    }

    @Override
    public Subtask tryToDelete(UUID id, String token) throws InvalidJWTException, PermissionDeniedException, NotFoundException {
        if (jwtService.hasExpired(token)) {
            throw new InvalidJWTException();
        }
        var maybeSubtask = source.findById(id);
        if (maybeSubtask.isEmpty()) {
            throw new NotFoundException();
        }
        var subtask = maybeSubtask.get();
        if (!userPermissionService.canDelete(subtask, jwtService.parse(token))) {
            throw new PermissionDeniedException();
        }
        source.delete(subtask);
        return subtask;
    }

    @Override
    public Collection<Subtask> getForTask(UUID id, String token) {
        return source.findByTask(id);
    }
}
