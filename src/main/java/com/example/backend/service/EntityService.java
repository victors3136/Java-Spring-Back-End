package com.example.backend.service;

import com.example.backend.exceptions.HttpTokenException;

import java.util.Collection;
import java.util.UUID;

public interface EntityService<Generic> {

    Collection<Generic> getAll(String token) throws HttpTokenException;

    Generic getById(UUID id, String token) throws HttpTokenException;

    Generic save(Generic entity, String token) throws HttpTokenException;
}
