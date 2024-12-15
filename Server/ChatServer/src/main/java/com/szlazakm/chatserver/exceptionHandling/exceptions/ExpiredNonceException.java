package com.szlazakm.chatserver.exceptionHandling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExpiredNonceException extends ResponseStatusException {

    public ExpiredNonceException() {
        super(HttpStatus.BAD_REQUEST, "Nonce is expired. Timestamp is older than 5 minutes.");
    }
}
