package org.sergp.bookingservice.exceptions;

public class BookingNotAvailableException extends RuntimeException {
    public BookingNotAvailableException(String message) {
        super(message);
    }
}
