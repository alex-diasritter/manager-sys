package com.managersys.exception;

import org.springframework.http.HttpStatus;

public class ResourceExistsException extends BaseException {
    public ResourceExistsException(String resource, String field, Object value) {
        super(HttpStatus.CONFLICT, "RESOURCE_EXISTS", 
              String.format("%s with %s '%s' already exists", resource, field, value));
    }

    public ResourceExistsException(String message) {
        super(HttpStatus.CONFLICT, "RESOURCE_EXISTS", message);
    }
}
