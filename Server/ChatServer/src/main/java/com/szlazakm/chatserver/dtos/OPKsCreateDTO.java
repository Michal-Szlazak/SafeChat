package com.szlazakm.chatserver.dtos;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
@ToString
public class OPKsCreateDTO {

    String phoneNumber;
    List<OPKCreateDTO> opkCreateDTOs;
}
