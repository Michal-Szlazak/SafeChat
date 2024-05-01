package com.szlazakm.chatserver.dtos;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
@ToString
public class OPKCreateDTO {

    int id;
    byte[] preKey;
}
