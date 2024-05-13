package com.example.backend.user_requests;

import java.time.Instant;

public record LoginRequest(String username, String password, Instant time) {}