package com.example.backend.service;

import com.example.backend.exceptions.ApplicationException;
import com.example.backend.model.Subtask;
import com.example.backend.repository.SubtaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

import static com.example.backend.exceptions.FailureReason.*;

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
    public Collection<Subtask> getAll(String token) throws ApplicationException {
        if (jwtService.hasExpired(token)) {
            throw new ApplicationException(JWT_EXPIRED);
        }
        if (!userPermissionService.canRead(jwtService.parse(token))) {
            throw new ApplicationException(PERMISSION_DENIED);
        }
        return source.findAll();
    }

    @Override
    public Subtask getById(UUID id, String token) throws ApplicationException {
        if (jwtService.hasExpired(token)) {
            throw new ApplicationException(JWT_EXPIRED);
        }
        if (!userPermissionService.canRead(jwtService.parse(token))) {
            throw new ApplicationException(PERMISSION_DENIED);
        }
        var subtask = source.findById(id);
        if (subtask.isEmpty()) {
            throw new ApplicationException(NOT_FOUND);
        }
        return subtask.get();
    }

    @Override
    public Subtask save(Subtask entity, String token) throws ApplicationException {
        if (jwtService.hasExpired(token)) {
            throw new ApplicationException(JWT_EXPIRED);
        }
        if (!userPermissionService.canCreate(jwtService.parse(token))) {
            throw new ApplicationException(PERMISSION_DENIED);
        }
        if (entity.validationFails()) {
            throw new ApplicationException(VALIDATION_FAILED);
        }
        Subtask newSubtask = new Subtask();
        newSubtask.setSubject(entity.getSubject());
        newSubtask.setTask(entity.getTask());
        return source.save(newSubtask);
    }

    @Override
    public Page<Subtask> getPage(int pageNumber,
                                 int pageSize,
                                 UUID taskID,
                                 String token) throws ApplicationException {
        if (pageNumber < 0 || pageSize < 0 || taskID == null || token == null) {
            throw new ApplicationException(VALIDATION_FAILED,
                    "Requesting an absurd scenario -- page nr: %d, page size: %d, taskID: %s, token: %s"
                            .formatted(pageNumber, pageSize, taskID, token));
        }
        if (jwtService.hasExpired(token)) {
            throw new ApplicationException(JWT_EXPIRED);
        }
        if (!userPermissionService.canCreate(jwtService.parse(token))) {
            throw new ApplicationException(PERMISSION_DENIED);
        }
        return source.findAll(
                (subtask, query, where) -> where
                        .equal(subtask.get("task"),
                                taskID),
                PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public long countSubtasksByTask(UUID id, String token) throws ApplicationException {
        if (jwtService.hasExpired(token)) {
            throw new ApplicationException(JWT_EXPIRED);
        }
        if (!userPermissionService.canRead(jwtService.parse(token))) {
            throw new ApplicationException(PERMISSION_DENIED);
        }
        return source.countSubtasksByTask(id);
    }

    @Override
    public Subtask tryToUpdate(UUID id, Subtask subtask, String token) throws ApplicationException {
        if (jwtService.hasExpired(token)) {
            System.out.println("Expired jwt");
            throw new ApplicationException(JWT_EXPIRED);
        }
        if (!userPermissionService.canUpdate(jwtService.parse(token), subtask)) {
            System.out.println("Can't update");
            throw new ApplicationException(PERMISSION_DENIED);
        }
        if (subtask.validationFails()) {
            System.out.println("Failed validation");
            throw new ApplicationException(VALIDATION_FAILED);
        }
        var maybeSubtask = source.findById(id);
        if (maybeSubtask.isEmpty()) {
            System.out.println("Not found");
            throw new ApplicationException(NOT_FOUND);
        }
        var toUpdate = maybeSubtask.get();
        Subtask updated = new Subtask();
        updated.setId(toUpdate.getId());
        updated.setSubject(subtask.getSubject());
        updated.setTask(subtask.getTask());
        return source.save(updated);
    }

    @Override
    public Subtask tryToDelete(UUID id, String token) throws ApplicationException {
        if (jwtService.hasExpired(token)) {
            throw new ApplicationException(JWT_EXPIRED);
        }
        var maybeSubtask = source.findById(id);
        if (maybeSubtask.isEmpty()) {
            throw new ApplicationException(NOT_FOUND);
        }
        var subtask = maybeSubtask.get();
        if (!userPermissionService.canDelete(subtask, jwtService.parse(token))) {
            throw new ApplicationException(PERMISSION_DENIED);
        }
        source.delete(subtask);
        return subtask;
    }

    @Override
    public Collection<Subtask> getSubtasksForTask(UUID id, String token) throws ApplicationException {
        if (jwtService.hasExpired(token)) {
            throw new ApplicationException(JWT_EXPIRED);
        }
        if (!userPermissionService.canRead(jwtService.parse(token))) {
            throw new ApplicationException(PERMISSION_DENIED);
        }
        return source.findByTask(id);
    }
}
