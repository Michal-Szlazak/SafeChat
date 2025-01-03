package com.szlazakm.chatserver.exceptionHandling;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private String errorMessage;

    public ApiError(String errorMessage) {
        this.errorMessage = errorMessage;
        timestamp = LocalDateTime.now();
    }
}
