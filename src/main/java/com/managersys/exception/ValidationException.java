package com.managersys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends BaseException {
    private final Map<String, String> validationErrors;

    public ValidationException(String message, Map<String, String> validationErrors) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
        this.validationErrors = validationErrors;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}
