package com.szlazakm.chatserver.dtos.response;

import com.szlazakm.chatserver.dtos.EncryptedMessageDTO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Value
@Builder
public class OutputEncryptedMessageDTO {
    boolean initial;
    UUID id;
    String from;
    String to;
    String cipher;
    String aliceIdentityPublicKey;
    String aliceEphemeralPublicKey;
    Integer bobOpkId;
    Integer bobSpkId;
    String ephemeralRatchetKey;
    int messageIndex;
    int lastMessageBatchSize;
    String date;
}
