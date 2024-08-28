package org.sergp.apigateway.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.sergp.apigateway.dto.ExceptionResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BearerTokenException.class)
    public ResponseEntity<?> handleBearerTokenException(BearerTokenException ex, WebRequest request) {
        logError(ex, request);
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> handleAuthorizationException(AuthorizationException ex, WebRequest request) {
        logError(ex, request);
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleAuthorizationException(ForbiddenException ex, WebRequest request) {
        logError(ex, request);
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex, WebRequest request) {
        logError(ex, request);
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        return buildErrorResponse(ex.getMessage(), status);
    }







    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        ExceptionResponseModel response = new ExceptionResponseModel(
                status.toString(), message, "Error details", null, new Date()
        );
        return new ResponseEntity<>(response, status);
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
