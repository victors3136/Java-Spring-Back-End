package com.example.backend.model;

import java.util.UUID;

public interface HasId {
    UUID getId();

    void setId(UUID newId);
}
