package com.example.backend.service;

import com.example.backend.exceptions.ValidationException;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EntityService<Generic> {

    Collection<Generic> getAll(String token);

    Optional<Generic> getById(UUID id, String token);

    Generic save(Generic entity, String token) throws ValidationException;
}
