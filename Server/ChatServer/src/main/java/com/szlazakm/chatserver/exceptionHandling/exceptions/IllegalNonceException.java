package com.szlazakm.chatserver.exceptionHandling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IllegalNonceException extends ResponseStatusException {

    public IllegalNonceException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
