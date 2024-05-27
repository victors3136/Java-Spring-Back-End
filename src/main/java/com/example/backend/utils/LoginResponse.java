package com.example.backend.utils;

import java.util.List;

public record LoginResponse(String token, List<String> permissions) {
}
