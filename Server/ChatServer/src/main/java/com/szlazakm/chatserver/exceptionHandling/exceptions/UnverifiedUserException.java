package com.szlazakm.chatserver.exceptionHandling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnverifiedUserException extends ResponseStatusException {

    public UnverifiedUserException() {
        super(HttpStatus.BAD_REQUEST, "User is not verified.");
    }
}
