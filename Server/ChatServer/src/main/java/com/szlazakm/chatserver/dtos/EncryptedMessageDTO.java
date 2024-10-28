package com.szlazakm.chatserver.dtos;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder
@Jacksonized
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EncryptedMessageDTO {
    boolean initial;
    UUID id;
    String from;
    String to;
    String cipher;
    String aliceIdentityPublicKey;
    String aliceEphemeralPublicKey;
    Integer bobOpkId;
    Integer bobSpkId;
    String ephemeralRatchetKey;
    int messageIndex;
    int lastMessageBatchSize;
}
