package com.flownocode.api.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, Object id) {
        super(resource + " already exists with id: " + id);
    }
}
