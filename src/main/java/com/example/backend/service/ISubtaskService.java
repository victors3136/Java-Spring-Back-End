package com.example.backend.service;

import com.example.backend.exceptions.InvalidJWTException;
import com.example.backend.exceptions.NotFoundException;
import com.example.backend.exceptions.PermissionDeniedException;
import com.example.backend.exceptions.ValidationException;
import com.example.backend.model.Subtask;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ISubtaskService extends EntityService<Subtask> {
    @SuppressWarnings("unused")
    Optional<Page<Subtask>> getPage(int pageNumber, int pageSize, UUID taskID, String token);

    long countSubtasksByTask(UUID id, String token) throws InvalidJWTException, PermissionDeniedException;

    Subtask tryToUpdate(UUID id, Subtask subtask, String token) throws InvalidJWTException, PermissionDeniedException, NotFoundException, ValidationException;

    Subtask tryToDelete(UUID id, String token) throws InvalidJWTException, PermissionDeniedException, NotFoundException;

    Collection<Subtask> getForTask(UUID id, String token);

}
