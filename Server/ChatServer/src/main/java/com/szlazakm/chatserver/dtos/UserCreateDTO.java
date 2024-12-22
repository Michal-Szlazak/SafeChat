package com.szlazakm.chatserver.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@SuperBuilder
@Jacksonized
public class UserCreateDTO {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]")
    String firstName;
    String lastName;
    String identityKey;
    String pin;
    String phoneNumber;

}
