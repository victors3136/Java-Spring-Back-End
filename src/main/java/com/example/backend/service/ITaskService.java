package com.example.backend.service;

import com.example.backend.model.Task;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ITaskService extends EntityService<Task> {
    boolean taskBelongsToTokenHolder(Task task, String token);

    @SuppressWarnings("unused")
    Collection<Task> getAllForTokenHolder(String token);

    Optional<Task> getForTokenHolderById(UUID id, String token);

    Optional<Page<Task>> getPage(int number, int size, String token);

    boolean tryToUpdate(UUID id, Task task, String userToken);

    @SuppressWarnings("unused")
    Task save(Task newTask, String token);

    boolean tryToDelete(UUID id, String userToken);

    boolean batchDelete(Collection<UUID> ids, String userToken);
}
