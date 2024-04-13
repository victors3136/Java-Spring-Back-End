package com.example.backend.repository;

import com.example.backend.model.HasId;

import java.util.*;

public class InMemoryRepository<T extends HasId> implements Repository<T> {
    private final Map<UUID, T> data = new HashMap<>();

    @Override
    public List<T> getAll() {
        return data.values().stream().toList();
    }

    @Override
    public Optional<T> get(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public UUID post(T newItem) {
        if (newItem == null) {
            return null;
        }
        do {
            newItem.setId(UUID.randomUUID());
        } while (data.values().stream().anyMatch(x -> x.getId().equals(newItem.getId())));
        data.put(newItem.getId(), newItem);
        return newItem.getId();
    }

    @Override
    public boolean delete(UUID id) {
        return data.remove(id) != null;
    }

    @Override
    public boolean patch(UUID id, T newFields) {
        if (newFields == null) {
            return false;
        }
        if (!data.containsKey(id)) {
            return false;
        }
        newFields.setId(id);
        data.put(id, newFields);
        return true;
    }

    @Override
    public Optional<T> getById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }
}
