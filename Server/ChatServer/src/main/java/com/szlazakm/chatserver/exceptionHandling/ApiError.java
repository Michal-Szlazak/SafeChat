package com.szlazakm.chatserver.exceptionHandling;

import lombok.Data;

@Data
public class ApiError {

    private String errorMessage;

    public ApiError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
