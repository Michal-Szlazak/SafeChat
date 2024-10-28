package com.szlazakm.chatserver.dtos.response;

import com.szlazakm.chatserver.dtos.EncryptedMessageDTO;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
public class OutputEncryptedMessageDTO extends EncryptedMessageDTO {

    String date;

    public OutputEncryptedMessageDTO(
            final UUID id,
            final boolean initial,
            final String from,
            final String to,
            final String cipher,
            String aliceIdentityPublicKey,
            String aliceEphemeralPublicKey,
            Integer bobOpkId,
            Integer bobSpkId,
            final String date,
            String ephemeralRatchetKey,
            int messageIndex,
            int lastMessageBatchSize
    ) {
        this.setInitial(initial);
        this.setId(id);
        this.setFrom(from);
        this.setTo(to);
        this.setCipher(cipher);
        this.setAliceIdentityPublicKey(aliceIdentityPublicKey);
        this.setAliceEphemeralPublicKey(aliceEphemeralPublicKey);
        this.setBobOpkId(bobOpkId);
        this.setBobSpkId(bobSpkId);
        this.date = date;
        this.setEphemeralRatchetKey(ephemeralRatchetKey);
        this.setMessageIndex(messageIndex);
        this.setLastMessageBatchSize(lastMessageBatchSize);
    }

    public OutputEncryptedMessageDTO(
            final UUID id,
            final boolean isInitial,
            final String from,
            final String to,
            final String cipher,
            final String date,
            String ephemeralRatchetKey,
            int messageIndex,
            int lastMessageBatchSize
    ) {
        this.setInitial(isInitial);
        this.setId(id);
        this.setFrom(from);
        this.setTo(to);
        this.setCipher(cipher);
        this.date = date;
        this.setEphemeralRatchetKey(ephemeralRatchetKey);
        this.setMessageIndex(messageIndex);
        this.setLastMessageBatchSize(lastMessageBatchSize);
    }
}
