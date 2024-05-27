package com.example.backend.service;

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

    public SubtaskService(SubtaskRepository source) {
        this.source = source;
    }

    @Override
    public Collection<Subtask> getAll() {
        return source.findAll();
    }

    @Override
    public Optional<Subtask> getById(UUID id) {
        return source.findById(id);
    }

    @Override
    public Subtask save(Subtask entity) {
        Subtask newSubtask = new Subtask();
        newSubtask.setSubject(entity.getSubject());
        newSubtask.setTask(entity.getTask());
        return source.save(newSubtask);
    }

    @Override
    public Optional<Subtask> update(Subtask entity) {
        source.updateSubtaskById(
                entity.getId(),
                entity.getSubject(),
                entity.getTask());
        return source.findById(entity.getId());
    }

    @Override
    public Subtask delete(Subtask entity) {
        source.deleteById(entity.getId());
        return entity;
    }

    @Override
    public Optional<Page<Subtask>> getPage(int pageNumber, int pageSize, UUID taskID) {
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
    public long countSubtasksByTask(UUID id) {
        return source.countSubtasksByTask(id);
    }

    @Override
    public boolean tryToUpdate(UUID id, Subtask subtask) {
        return source.findById(id)
                .map(toUpdate -> {
                    Subtask updated = new Subtask();
                    updated.setId(toUpdate.getId());
                    updated.setSubject(subtask.getSubject());
                    updated.setTask(subtask.getTask());
                    source.save(updated);
                    return true;
                })
                .orElse(false);

    }

    @Override
    public boolean tryToDelete(UUID id) {
        return source.findById(id)
                .map(subtask -> {
                    source.delete(subtask);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Collection<Subtask> getForTask(UUID id) {
        return source.findByTask(id);
    }
}
