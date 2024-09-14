package com.szlazakm.chatserver.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserCreateDTO {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]")
    String firstName;
    String lastName;
    String phoneNumber;
    String identityKey;
    String pin;
}
