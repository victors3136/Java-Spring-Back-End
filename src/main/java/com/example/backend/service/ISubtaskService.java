package com.example.backend.service;

import com.example.backend.exceptions.ApplicationException;
import com.example.backend.model.Subtask;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.UUID;

public interface ISubtaskService extends EntityService<Subtask> {
    @SuppressWarnings("unused")
    Page<Subtask> getPage(int pageNumber, int pageSize, UUID taskID, String token) throws ApplicationException;

    long countSubtasksByTask(UUID id, String token) throws ApplicationException;

    Subtask tryToUpdate(UUID id, Subtask subtask, String token) throws ApplicationException;

    @SuppressWarnings("UnusedReturnValue")
    Subtask tryToDelete(UUID id, String token) throws ApplicationException;

    Collection<Subtask> getSubtasksForTask(UUID id, String token) throws ApplicationException;

}
