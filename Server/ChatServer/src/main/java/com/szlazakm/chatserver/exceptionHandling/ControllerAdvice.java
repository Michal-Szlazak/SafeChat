package com.szlazakm.chatserver.exceptionHandling;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String mainErrorMessage = "Data validation error";

        return new ResponseEntity<>(
                new ApiError(mainErrorMessage),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(DataIntegrityViolationException ex) {
        String mainErrorMessage = "Data integrity violation error: " + ex.getMessage();

        return new ResponseEntity<>(
                new ApiError(mainErrorMessage),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(ResponseStatusException ex) {

        return new ResponseEntity<>(
                new ApiError(ex.getMessage()),
                ex.getStatusCode());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(Exception ex) {
        String mainErrorMessage = "Unexpected error: ";

        return new ResponseEntity<>(new ApiError(mainErrorMessage + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        String errorMessage = "Missing header exception: " + ex.getHeaderName();

        return new ResponseEntity<>(new ApiError(errorMessage), HttpStatus.BAD_REQUEST);
    }
}
