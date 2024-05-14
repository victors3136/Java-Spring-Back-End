package com.example.backend.requests;

public record ChangePasswordRequest(String username, String oldPassword, String newPassword){}
