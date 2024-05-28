package com.example.backend.service;

import com.example.backend.exceptions.HttpTokenException;
import com.example.backend.model.Task;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ITaskService extends EntityService<Task> {
    boolean taskBelongsToTokenHolder(Task task, String token);

    Page<Task> getPage(int number, int size, String token) throws HttpTokenException;

    Task tryToUpdate(UUID id, Task task, String userToken) throws HttpTokenException;

    void tryToDelete(UUID id, String userToken) throws HttpTokenException;

    void batchDelete(List<UUID> ids, String userToken) throws HttpTokenException;
}
