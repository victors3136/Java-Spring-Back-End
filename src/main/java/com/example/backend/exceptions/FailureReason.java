package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;

public enum FailureReason {
    VALIDATION_FAILED,
    JWT_EXPIRED,
    PERMISSION_DENIED,
    NOT_FOUND;

    public HttpStatus asHttp() {
        return switch (this) {
            case VALIDATION_FAILED -> HttpStatus.BAD_REQUEST;
            case JWT_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
    }

    public String asString() {
        return switch (this) {
            case VALIDATION_FAILED -> "Failed server side validation";
            case JWT_EXPIRED -> "Session expired. Log in again to continue";
            case PERMISSION_DENIED -> "Permission denied";
            case NOT_FOUND -> "Requested resource could not be located";
        };
    }
}
