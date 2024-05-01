package com.szlazakm.chatserver.dtos;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class KeyBundleDTO {

    byte[] identityKey;

    int signedPreKeyId;
    byte[] signedPreKey;
    byte[] signature;

    int onetimePreKeyId;
    byte[] onetimePreKey;
}
