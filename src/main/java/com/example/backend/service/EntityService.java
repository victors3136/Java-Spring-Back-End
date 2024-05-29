package com.example.backend.service;

import com.example.backend.exceptions.ApplicationException;

import java.util.Collection;
import java.util.UUID;

public interface EntityService<Generic> {

    Collection<Generic> getAll(String token) throws ApplicationException;

    Generic getById(UUID id, String token) throws ApplicationException;

    Generic save(Generic entity, String token) throws ApplicationException;
}
