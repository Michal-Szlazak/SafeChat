package com.szlazakm.chatserver.dtos;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.whispersystems.libsignal.IdentityKey;

@Value
@Builder
@Jacksonized
public class UserCreateDTO {

    String firstName;
    String lastName;
    String phoneNumber;
    byte[] identityKey;
    String pin;
}
