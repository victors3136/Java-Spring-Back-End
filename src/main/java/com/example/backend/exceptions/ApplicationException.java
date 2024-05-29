package com.example.backend.exceptions;

import org.springframework.http.ResponseEntity;

public class ApplicationException extends Exception {
    private final FailureReason reason;
    private final String message;

    public ApplicationException(FailureReason reason, String message) {
        this.reason = reason;
        this.message = message;
        System.out.println(this.reason.asString() + this.reason.asHttp().toString());

    }

    public ApplicationException(FailureReason reason) {
        this.reason = reason;
        this.message = reason.asString();
    }

    public FailureReason status() {
        return reason;
    }

    public String message() {
        return message;
    }

    public ResponseEntity<Void> asHttpResponse() {
        return ResponseEntity.status(reason.asHttp()).build();
    }
}
