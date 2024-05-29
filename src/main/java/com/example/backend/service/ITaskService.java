package com.example.backend.service;

import com.example.backend.exceptions.ApplicationException;
import com.example.backend.model.Task;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ITaskService extends EntityService<Task> {
    boolean taskBelongsToTokenHolder(Task task, String token);

    Page<Task> getPage(int number, int size, String token) throws ApplicationException;

    Task tryToUpdate(UUID id, Task task, String userToken) throws ApplicationException;

    void tryToDelete(UUID id, String userToken) throws ApplicationException;

    void batchDelete(List<UUID> ids, String userToken) throws ApplicationException;
}
