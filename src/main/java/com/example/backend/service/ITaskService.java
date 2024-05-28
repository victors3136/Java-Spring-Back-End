package com.example.backend.service;

import com.example.backend.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITaskService extends EntityService<Task> {
    boolean taskBelongsToTokenHolder(Task task, String token);

    Optional<Task> getForTokenHolderById(UUID id, String token);

    Optional<Page<Task>> getPage(int number, int size, String token);

    HttpStatus tryToUpdate(UUID id, Task task, String userToken);

    Task save(Task task, String userToken);

    HttpStatus tryToDelete(UUID id, String userToken);

    HttpStatus batchDelete(List<UUID> ids, String userToken);
}
