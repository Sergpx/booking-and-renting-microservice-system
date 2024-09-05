package org.sergp.bookingservice.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.sergp.bookingservice.dto.Violation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logError(ex, request);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());

    }

    @ExceptionHandler(BookingNotAvailableException.class)
    public ResponseEntity<?> handleBookingNotAvailableException(BookingNotAvailableException ex, WebRequest request) {
        logError(ex, request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<?> handleBookingNotFoundException(BookingNotFoundException ex, WebRequest request) {
        logError(ex, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IncorrectDateException.class)
    public ResponseEntity<?> handleIncorrectDateException(IncorrectDateException ex, WebRequest request) {
        logError(ex, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(WrongStatusException.class)
    public ResponseEntity<?> handleWrongStatusException(WrongStatusException ex, WebRequest request) {
        logError(ex, request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> onConstraintValidationException(ConstraintViolationException ex) {
        final List<Violation> violations = ex.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> onMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        final List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(violations);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> onDataIntegrityViolationException(DataIntegrityViolationException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data integrity violation: " + ex.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, WebRequest request) {
        logError(ex, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }



// private methods
    private void logError(Exception ex, WebRequest request) {
        log.error("Error occurred", Map.of(
                "requestURI", request.getDescription(false),
                "errorMessage", ex.getMessage(),
                "stackTrace", Arrays.toString(ex.getStackTrace())
        ));
    }

}
