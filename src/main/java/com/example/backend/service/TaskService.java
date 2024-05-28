package com.example.backend.service;

import com.example.backend.model.Task;
import com.example.backend.repository.TaskRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

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
    public Collection<Task> getAll(String token) {
        return taskSource.findAll();
    }

    @Override
    public Optional<Task> getById(UUID id, String token) {
        return taskSource.findById(id);
    }

    @Override
    public Optional<Task> getForTokenHolderById(UUID id, String token) {
        return taskSource.findById(id)
                .filter(task -> taskBelongsToTokenHolder(task, token));
    }

    @Override
    public Optional<Page<Task>> getPage(int pageNumber, int pageSize, String userToken) {
        if (pageNumber < 0 || pageSize < 0 || userToken == null) {
            return Optional.empty();
        }
        return Optional.of(
                taskSource.findAll(
                        (task, query, where) -> where
                                .equal(task.get("user"),
                                        jwtService.parse(userToken)),
                        PageRequest.of(pageNumber, pageSize)));
    }

    @Override
    public HttpStatus tryToUpdate(UUID id, Task task, String userToken) {
        if (jwtService.hasExpired(userToken)) {
            return HttpStatus.NETWORK_AUTHENTICATION_REQUIRED;
        }
        return taskSource.findById(id)
                .filter(toUpdate -> taskBelongsToTokenHolder(toUpdate, userToken))
                .map(toUpdate -> {
                    Task updated = new Task();
                    updated.setId(toUpdate.getId());
                    updated.setName(task.getName());
                    updated.setDescription(task.getDescription());
                    updated.setPriority(task.getPriority());
                    updated.setDueDate(task.getDueDate());
                    updated.setUser(jwtService.parse(userToken));
                    taskSource.save(updated);
                    return OK;
                })
                .orElse(NOT_FOUND);
    }

    @Override
    public Task save(Task task, String userToken) {
        if (jwtService.hasExpired(userToken)) {
            return null;
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
    public HttpStatus tryToDelete(UUID id, String userToken) {
        if (jwtService.hasExpired(userToken)) {
            return HttpStatus.NETWORK_AUTHENTICATION_REQUIRED;
        }
        var maybeTask = taskSource.findById(id);
        if (maybeTask.isEmpty()) {
            return NOT_FOUND;
        }
        var task = maybeTask.get();
        var user = jwtService.parse(userToken);
        if (!userPermissionService.canDelete(task, user)) {
            return HttpStatus.UNAUTHORIZED;
        }
        taskSource.delete(task);
        return HttpStatus.NO_CONTENT;
    }

    @Override
    public HttpStatus batchDelete(List<UUID> ids, String userToken) {
        if (jwtService.hasExpired(userToken)) {
            return HttpStatus.NETWORK_AUTHENTICATION_REQUIRED;
        }
        var user = jwtService.parse(userToken);
        List<String> permissions = users.findPermissionsByUserId(user);
        if (!permissions.contains("delete-batch")) {
            return HttpStatus.UNAUTHORIZED;
        }
        var idsOfTasksThatCanBeDeleted = taskSource.findAllById(ids)
                .stream()
                .filter(task -> userPermissionService.canDelete(task, user, permissions))
                .toList();
        if (idsOfTasksThatCanBeDeleted.isEmpty()) {
            return NOT_FOUND;
        }
        taskSource.deleteAllInBatch(idsOfTasksThatCanBeDeleted);
        return HttpStatus.NO_CONTENT;
    }
}
