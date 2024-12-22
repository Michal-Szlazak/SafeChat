package com.szlazakm.chatserver.dtos;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.UUID;

@SuperBuilder
@Jacksonized
@Data
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
    String phoneNumber;
    Long nonceTimestamp;
    byte[] nonce;
    byte[] authMessageSignature;
}
