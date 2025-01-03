package com.szlazakm.chatserver.dtos;


import lombok.Data;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Value
@SuperBuilder
@Jacksonized
public class SPKCreateOrUpdateDTO {

    int id;
    String signedPreKey;
    String signature;
    Long timestamp;

    String phoneNumber;
    Long nonceTimestamp;
    byte[] nonce;
    byte[] authMessageSignature;
}
