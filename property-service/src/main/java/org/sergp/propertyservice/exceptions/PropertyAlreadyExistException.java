package org.sergp.propertyservice.exceptions;

public class PropertyAlreadyExistException extends RuntimeException {
    public PropertyAlreadyExistException(String message) {
        super(message);
    }
}
