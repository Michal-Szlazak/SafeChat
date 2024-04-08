package com.szlazakm.chatserver.exceptionHandling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SignatureVerifierException extends ResponseStatusException {

    public SignatureVerifierException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
