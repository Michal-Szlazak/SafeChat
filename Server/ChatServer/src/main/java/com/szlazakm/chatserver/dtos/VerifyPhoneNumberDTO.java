package com.szlazakm.chatserver.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class VerifyPhoneNumberDTO {

    @NotNull
    @Pattern(regexp = "^\\d{4}$", message = "The code has to contain 4 digits")
    String code;

    String phoneNumber;
}
