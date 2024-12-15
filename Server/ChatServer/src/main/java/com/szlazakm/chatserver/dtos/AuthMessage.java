package com.szlazakm.chatserver.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Builder
@Jacksonized
@AllArgsConstructor
@Data
public class AuthMessage {

    private Instant timestamp;
    private String nonce;
}
