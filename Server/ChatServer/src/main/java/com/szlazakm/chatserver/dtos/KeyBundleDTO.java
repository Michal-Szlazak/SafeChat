package com.szlazakm.chatserver.dtos;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class KeyBundleDTO {

    String identityKey;
    String signedPreKey;
    String signature;
    String onetimePreKey;
}
