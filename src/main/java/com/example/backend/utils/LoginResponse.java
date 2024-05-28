package com.example.backend.utils;

import java.util.List;
import java.util.UUID;

public record LoginResponse(String token, UUID id, List<String> permissions) {
}
