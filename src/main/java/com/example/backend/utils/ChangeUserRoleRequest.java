package com.example.backend.utils;

import java.util.UUID;

public record ChangeUserRoleRequest(UUID id, String role) {
}
