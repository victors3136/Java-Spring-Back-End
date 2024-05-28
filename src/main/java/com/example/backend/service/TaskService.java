package com.example.backend.service;

import com.example.backend.exceptions.FailureReason;
import com.example.backend.exceptions.HttpTokenException;
import com.example.backend.model.Task;
import com.example.backend.repository.TaskRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.example.backend.exceptions.FailureReason.*;

@Service
public class TaskService implements ITaskService {
    private final JSONWebTokenService jwtService;
    private final TaskRepository taskSource;
    private final UserRepository users;
    private final UserPermissionService userPermissionService;

    @Autowired
    public TaskService(TaskRepository taskSource, JSONWebTokenService jwtService, UserRepository users, UserPermissionService userPermissionService) {
        this.taskSource = taskSource;
        this.jwtService = jwtService;
        this.users = users;
        this.userPermissionService = userPermissionService;
    }


    @Override
    public boolean taskBelongsToTokenHolder(Task task, String token) {
        return task.getUser().equals(jwtService.parse(token));
    }

    @Override
    public Collection<Task> getAll(String token) throws HttpTokenException {
        if (jwtService.hasExpired(token)) {
            throw new HttpTokenException(JWT_EXPIRED);
        }
        if (userPermissionService.canRead(jwtService.parse(token))) {
            throw new HttpTokenException(PERMISSION_DENIED);
        }
        return taskSource.findAll();
    }

    @Override
    public Task getById(UUID id, String token) throws HttpTokenException {
        if (jwtService.hasExpired(token)) {
            throw new HttpTokenException(JWT_EXPIRED);
        }
        if (userPermissionService.canRead(jwtService.parse(token))) {
            throw new HttpTokenException(PERMISSION_DENIED);
        }
        var task = taskSource.findById(id);
        if (task.isEmpty()) {
            throw new HttpTokenException(FailureReason.NOT_FOUND);
        }
        return task.get();
    }

    @Override
    public Page<Task> getPage(int pageNumber, int pageSize, String userToken) throws HttpTokenException {
        if (pageNumber < 0 || pageSize < 0 || userToken == null) {
            throw new HttpTokenException(VALIDATION_FAILED, "Requesting an absurd scenario -- page nr: %d, page size:%s, token :%s".formatted(pageNumber, pageSize, userToken));
        }
        if (jwtService.hasExpired(userToken)) {
            throw new HttpTokenException(JWT_EXPIRED);
        }
        if (!userPermissionService.canRead(jwtService.parse(userToken))) {
            throw new HttpTokenException(PERMISSION_DENIED);
        }
        return taskSource.findAll(
                (task, query, where) -> where.equal(task.get("user"), jwtService.parse(userToken)),
                PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Task tryToUpdate(UUID id, Task task, String userToken) throws HttpTokenException {
        if (task.validationFails()) {
            throw new HttpTokenException(VALIDATION_FAILED);
        }
        if (jwtService.hasExpired(userToken)) {
            throw new HttpTokenException(JWT_EXPIRED);
        }
        var toUpdate = taskSource.findById(id)
                .filter(maybeTask -> taskBelongsToTokenHolder(maybeTask, userToken));
        if (toUpdate.isEmpty()) {
            throw new HttpTokenException(FailureReason.NOT_FOUND);
        }
        if (!userPermissionService.canUpdate(jwtService.parse(userToken), toUpdate.get())) {
            throw new HttpTokenException(PERMISSION_DENIED);
        }
        Task updated = new Task();
        updated.setId(toUpdate.get().getId());
        updated.setName(task.getName());
        updated.setDescription(task.getDescription());
        updated.setPriority(task.getPriority());
        updated.setDueDate(task.getDueDate());
        updated.setUser(jwtService.parse(userToken));
        return taskSource.save(updated);
    }

    @Override
    public Task save(Task task, String userToken) throws HttpTokenException {
        if (jwtService.hasExpired(userToken)) {
            return null;
        }
        if (task.validationFails()) {
            throw new HttpTokenException(VALIDATION_FAILED);
        }
        UUID authorID = jwtService.parse(userToken);
        if (!userPermissionService.canCreate(authorID)) {
            Task newTask = new Task();
            newTask.setId(null);
            return newTask;
        }
        Task newTask = new Task();
        newTask.setName(task.getName());
        newTask.setDescription(task.getDescription());
        newTask.setPriority(task.getPriority());
        newTask.setDueDate(task.getDueDate());
        newTask.setUser(authorID);
        return taskSource.save(newTask);
    }

    @Override
    public void tryToDelete(UUID id, String userToken) throws HttpTokenException {
        if (jwtService.hasExpired(userToken)) {
            throw new HttpTokenException(JWT_EXPIRED);
        }
        var maybeTask = taskSource.findById(id);
        if (maybeTask.isEmpty()) {
            throw new HttpTokenException(FailureReason.NOT_FOUND);
        }
        var task = maybeTask.get();
        var user = jwtService.parse(userToken);
        if (!userPermissionService.canDelete(task, user)) {
            throw new HttpTokenException(PERMISSION_DENIED);
        }
        taskSource.delete(task);
    }

    @Override
    public void batchDelete(List<UUID> ids, String userToken) throws HttpTokenException {
        if (jwtService.hasExpired(userToken)) {
            throw new HttpTokenException(JWT_EXPIRED);
        }
        var user = jwtService.parse(userToken);
        List<String> permissions = users.findPermissionsByUserId(user);
        if (!userPermissionService.canDeleteBatch(user)) {
            throw new HttpTokenException(PERMISSION_DENIED);
        }
        var idsOfTasksThatCanBeDeleted = taskSource.findAllById(ids)
                .stream()
                .filter(task -> userPermissionService.canDelete(task, user, permissions))
                .toList();
        if (idsOfTasksThatCanBeDeleted.isEmpty()) {
            throw new HttpTokenException(FailureReason.NOT_FOUND);
        }
        taskSource.deleteAllInBatch(idsOfTasksThatCanBeDeleted);
    }
}
