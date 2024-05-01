package com.szlazakm.chatserver.dtos;


import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class SPKCreateDTO {

    String phoneNumber;
    int id;
    byte[] signedPreKey;
    byte[] signature;
    Long timestamp;
}
