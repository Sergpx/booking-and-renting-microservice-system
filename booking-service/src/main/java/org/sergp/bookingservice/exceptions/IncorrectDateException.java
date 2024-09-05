package org.sergp.bookingservice.exceptions;

public class IncorrectDateException extends RuntimeException {
  public IncorrectDateException(String message) {
    super(message);
  }
}
