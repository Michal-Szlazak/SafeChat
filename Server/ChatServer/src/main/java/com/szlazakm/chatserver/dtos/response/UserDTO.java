package com.szlazakm.chatserver.dtos.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserDTO {

    String firstName;
    String lastName;
    String phoneNumber;
    String identityKey;
}