package com.example.backend.utils;

public record ChangePasswordRequest(String username, String oldPassword, String newPassword){}
