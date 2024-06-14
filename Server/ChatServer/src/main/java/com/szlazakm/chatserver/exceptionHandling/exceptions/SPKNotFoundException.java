package com.szlazakm.chatserver.exceptionHandling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SPKNotFoundException extends ResponseStatusException {
    public SPKNotFoundException() {
        super(HttpStatus.NOT_FOUND, "SPK not found.");
    }
}
