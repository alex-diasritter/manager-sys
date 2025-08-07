package com.managersys.exception;

import org.springframework.http.HttpStatus;

public class InvalidScheduleException extends BaseException {
    public InvalidScheduleException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_SCHEDULE", message);
    }
}
