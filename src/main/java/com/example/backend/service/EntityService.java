package com.example.backend.service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EntityService<Generic> {

    Collection<Generic> getAll();

    Optional<Generic> getById(UUID id);

    Generic save(Generic entity);

    @SuppressWarnings("unused")
    Optional<Generic> update(Generic entity);

    @SuppressWarnings("unused")
    Generic delete(Generic entity);
}
