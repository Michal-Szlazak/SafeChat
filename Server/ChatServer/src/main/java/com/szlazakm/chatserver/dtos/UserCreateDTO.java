package com.szlazakm.chatserver.dtos;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserCreateDTO {

    String firstName;
    String lastName;
    String phoneNumber;
    String identityKey;
    String pin;
}
