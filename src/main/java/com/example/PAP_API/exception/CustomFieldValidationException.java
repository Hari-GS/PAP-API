package com.example.PAP_API.exception;

import java.util.Map;

public class CustomFieldValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public CustomFieldValidationException(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
