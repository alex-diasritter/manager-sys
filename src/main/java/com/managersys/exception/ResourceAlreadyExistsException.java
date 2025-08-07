package com.managersys.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {
    public ResourceAlreadyExistsException(String resource, String field, Object value) {
        super(HttpStatus.CONFLICT, "RESOURCE_ALREADY_EXISTS", 
              String.format("%s with %s '%s' already exists", resource, field, value));
    }
}
