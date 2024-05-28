package com.example.backend.exceptions;

public class HttpTokenException extends Exception {
    private final FailureReason status;
    private final String message;

    public HttpTokenException(FailureReason status, String message) {
        this.status = status;
        this.message = message;
    }
    public HttpTokenException(FailureReason status) {
        this.status = status;
        this.message = status.mapToDefaultMessage();
    }

    public FailureReason status() {
        return status;
    }

    public String message() {
        return message;
    }

}
