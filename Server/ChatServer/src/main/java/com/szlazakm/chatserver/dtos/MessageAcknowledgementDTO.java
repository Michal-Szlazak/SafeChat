package com.szlazakm.chatserver.dtos;

import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@SuperBuilder
@Jacksonized
public class MessageAcknowledgementDTO {

    UUID messageId;
    String phoneNumber;
    Long nonceTimestamp;
    byte[] nonce;
    byte[] authMessageSignature;

}
