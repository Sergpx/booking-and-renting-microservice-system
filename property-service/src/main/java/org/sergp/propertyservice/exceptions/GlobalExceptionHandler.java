package org.sergp.propertyservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

    @ExceptionHandler(PropertyAlreadyExistException.class)
    public ResponseEntity<?> handlePropertyAlreadyExistException(PropertyAlreadyExistException ex, WebRequest request) {
        logError(ex, request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());

    }

    //TODO delete
//    @ExceptionHandler(EmptyResultDataAccessException.class)
//    public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
//        logError(ex, request);
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public ResponseEntity<?> handlePropertyNotFoundException(PropertyNotFoundException ex, WebRequest request) {
        logError(ex, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
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
