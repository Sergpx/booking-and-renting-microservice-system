package org.sergp.bookingservice.exceptions;

public class WrongStatusException extends RuntimeException {
    public WrongStatusException(String message) {
        super(message);
    }
}
