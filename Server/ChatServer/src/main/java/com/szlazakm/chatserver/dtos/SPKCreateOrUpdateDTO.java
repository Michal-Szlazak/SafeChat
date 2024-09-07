package com.szlazakm.chatserver.dtos;


import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SPKCreateOrUpdateDTO {

    String phoneNumber;
    int id;
    String signedPreKey;
    String signature;
    Long timestamp;
}
