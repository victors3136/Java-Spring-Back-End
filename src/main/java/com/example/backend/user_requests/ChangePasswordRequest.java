package com.example.backend.user_requests;

public record ChangePasswordRequest(String username, String oldPassword, String newPassword) {}
