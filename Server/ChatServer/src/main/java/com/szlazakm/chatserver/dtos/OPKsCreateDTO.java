package com.szlazakm.chatserver.dtos;

import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@SuperBuilder
@Jacksonized
@ToString
public class OPKsCreateDTO {

    List<OPKCreateDTO> opkCreateDTOs;
    String phoneNumber;
    Long nonceTimestamp;
    byte[] nonce;
    byte[] authMessageSignature;

}
