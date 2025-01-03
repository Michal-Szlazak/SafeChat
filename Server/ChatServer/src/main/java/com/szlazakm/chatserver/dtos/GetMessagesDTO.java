package com.szlazakm.chatserver.dtos;

import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@SuperBuilder
@Jacksonized
public class GetMessagesDTO {
    String phoneNumber;
    Long nonceTimestamp;
    byte[] nonce;
    byte[] authMessageSignature;
}
