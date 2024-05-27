package com.example.backend.utils;

@SuppressWarnings("unused")
public record ChangeRoleRequest(String issuerToken, String targetUsername, String newRoleName) {
}
