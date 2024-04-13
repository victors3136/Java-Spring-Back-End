package com.example.backend.repository;

import com.example.backend.model.HasId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T extends HasId> {
    List<T> getAll();

    @SuppressWarnings("unused")
    Optional<T> get(UUID id);

    UUID post(T newItem);

    boolean delete(UUID id);

    boolean patch(UUID id, T newFields);

    Optional<T> getById(UUID id);
}
