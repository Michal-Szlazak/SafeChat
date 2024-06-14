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
    String signedPreKey;
    String signature;
    Long timestamp;
}
