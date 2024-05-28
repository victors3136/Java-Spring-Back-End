package com.example.backend.model;

import java.util.UUID;

public record SimplifiedUser(UUID id, String username, String role) {
}
