package com.example.backend.service;

import com.example.backend.model.Subtask;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ISubtaskService extends EntityService<Subtask> {
    @SuppressWarnings("unused")
    Optional<Page<Subtask>> getPage(int pageNumber, int pageSize, UUID taskID);

    long countSubtasksByTask(UUID id);

    boolean tryToUpdate(UUID id, Subtask subtask);

    boolean tryToDelete(UUID id);

    Collection<Subtask> getForTask(UUID id);
}
