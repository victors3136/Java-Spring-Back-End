package com.example.backend.service;

import com.example.backend.model.Task;
import com.example.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService implements ITaskService {
    private final JSONWebTokenService jwtService;
    private final TaskRepository source;

    @Autowired
    public TaskService(TaskRepository source, JSONWebTokenService jwtService) {
        this.source = source;
        this.jwtService = jwtService;
    }

    @Override
    public boolean taskBelongsToTokenHolder(Task task, String token) {
        return task.getUser().equals(jwtService.parse(token));
    }

    @Override
    public Collection<Task> getAllForTokenHolder(String token) {
        return source.findByUserId(jwtService.parse(token));
    }

    @Override
    public Collection<Task> getAll() {
        return source.findAll();
    }

    @Override
    public Optional<Task> getById(UUID id) {
        return source.findById(id);
    }

    @Override
    public Optional<Task> getForTokenHolderById(UUID id, String token) {
        return source.findById(id)
                .filter(task -> taskBelongsToTokenHolder(task, token));
    }

    @Override
    public Optional<Page<Task>> getPage(int pageNumber, int pageSize, String userToken) {
        if (pageNumber < 0 || pageSize < 0 || userToken == null) {
            return Optional.empty();
        }
        return Optional.of(
                source.findAll(
                        (task, query, where) -> where
                                .equal(task.get("user"),
                                        jwtService.parse(userToken)),
                        PageRequest.of(pageNumber, pageSize)));
    }

    @Override
    public Task save(Task entity) {
        Task newTask = new Task();
        newTask.setUser(entity.getUser());
        newTask.setName(entity.getName());
        newTask.setDescription(entity.getDescription());
        newTask.setPriority(entity.getPriority());
        newTask.setDueDate(entity.getDueDate());
        return source.save(newTask);
    }

    @Override
    public Optional<Task> update(Task entity) {
        source.updateTaskById(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPriority(),
                entity.getDueDate(),
                entity.getUser());
        return source.findById(entity.getId());
    }

    @Override
    public Task delete(Task entity) {
        source.deleteById(entity.getId());
        return entity;
    }

    @Override
    public boolean tryToUpdate(UUID id, Task task, String userToken) {
        return source.findById(id)
                .filter(toUpdate -> taskBelongsToTokenHolder(toUpdate, userToken))
                .map(toUpdate -> {
                    Task updated = new Task();
                    updated.setId(toUpdate.getId());
                    updated.setName(task.getName());
                    updated.setDescription(task.getDescription());
                    updated.setPriority(task.getPriority());
                    updated.setDueDate(task.getDueDate());
                    updated.setUser(task.getUser());
                    source.save(updated);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Task save(Task task, String token) {
        UUID authorID = jwtService.parse(token);
        Task newTask = new Task();
        newTask.setName(task.getName());
        newTask.setDescription(task.getDescription());
        newTask.setPriority(task.getPriority());
        newTask.setDueDate(task.getDueDate());
        newTask.setUser(authorID);
        return source.save(newTask);
    }

    @Override
    public boolean tryToDelete(UUID id, String userToken) {
        return source.findById(id)
                .filter(task -> taskBelongsToTokenHolder(task, userToken))
                .map(task -> {
                    source.delete(task);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean batchDelete(Collection<UUID> ids, String userToken) {
        var idsOfTasksThatCanBeDeleted = source.findAllById(ids)
                .stream()
                .filter(task -> taskBelongsToTokenHolder(task, userToken))
                .toList();
        if (idsOfTasksThatCanBeDeleted.isEmpty()) {
            return false;
        }
        source.deleteAll(idsOfTasksThatCanBeDeleted);
        return true;
    }
}
