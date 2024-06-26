package com.szlazakm.chatserver.exceptionHandling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "User not found.");
    }
    public UserNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
