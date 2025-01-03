package com.szlazakm.chatserver.exceptionHandling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ReusedNonceException extends ResponseStatusException {

    public ReusedNonceException() {
        super(HttpStatus.BAD_REQUEST, "Nonce was already used.");
    }
}
