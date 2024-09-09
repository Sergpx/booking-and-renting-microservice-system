package org.sergp.paymentservice.exceptions;

public class UnknownWebHookException extends RuntimeException {
    public UnknownWebHookException(String message) {
        super(message);
    }
}
