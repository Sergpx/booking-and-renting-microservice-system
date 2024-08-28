package org.sergp.apigateway.exceptions;

public class BearerTokenException extends RuntimeException {
    public BearerTokenException(String message) {
        super(message);
    }
}
