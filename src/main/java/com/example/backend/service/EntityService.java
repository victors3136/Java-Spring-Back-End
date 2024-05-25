package com.example.backend.service;

import com.example.backend.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EntityService<Generic> {

    Collection<Generic> getAll();

    Optional<Generic> getById(UUID id);

    Generic save(Generic entity);

    Optional<User> update(Generic entity);

    User delete(Generic entity);
}
