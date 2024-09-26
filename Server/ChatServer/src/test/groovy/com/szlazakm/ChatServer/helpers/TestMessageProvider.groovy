package com.szlazakm.ChatServer.helpers

import com.szlazakm.chatserver.dtos.EncryptedMessageDTO
import com.szlazakm.chatserver.dtos.response.OutputEncryptedMessageDTO
import com.szlazakm.chatserver.entities.Message

class TestMessageProvider {

    static Message getCorrectMessage() {
        new Message(
                messageId: UUID.randomUUID(),
                isInitial: true,
                fromPhoneNumber: "123123123",
                toPhoneNumber: "321321321",
                cipher: "exampleCipher",
                aliceIdentityPublicKey: "idKey",
                aliceEphemeralPublicKey: "ephKey",
                bobOpkId: 1,
                bobSpkId: 1,
                timestamp: "time"
        )
    }

    static List<Message> getMessageList() {
        List.of(
                getCorrectMessage(),
                getCorrectMessage(),
                getCorrectMessage()
        )
    }

    static List<EncryptedMessageDTO> getEncryptedMessageListFromMessageList(List<Message> messages) {
         messages.stream().map(
                message -> new OutputEncryptedMessageDTO(
                        message.getMessageId(),
                        message.isInitial(),
                        message.getFromPhoneNumber(),
                        message.getToPhoneNumber(),
                        message.getCipher(),
                        message.getAliceIdentityPublicKey(),
                        message.getAliceEphemeralPublicKey(),
                        message.getBobOpkId(),
                        message.getBobSpkId(),
                        message.getTimestamp()
                )
        ).toList()
    }

    static EncryptedMessageDTO getEncryptedMessageDTO() {
        EncryptedMessageDTO.builder()
            .id(null)
            .initial(true)
            .from("123")
            .to("321")
            .cipher("cipher")
            .aliceIdentityPublicKey("alice identity key")
            .aliceEphemeralPublicKey("alice ephermal key")
            .bobSpkId(1)
            .bobOpkId(1)
            .build()
    }

    static Message getMessageFromEncryptedMessageDTO(EncryptedMessageDTO msg, String timestamp) {
        Message.builder()
                .isInitial(msg.isInitial())
                .toPhoneNumber(msg.getTo())
                .fromPhoneNumber(msg.getFrom())
                .cipher(msg.getCipher())
                .aliceIdentityPublicKey(msg.getAliceIdentityPublicKey())
                .aliceEphemeralPublicKey(msg.getAliceEphemeralPublicKey())
                .bobSpkId(msg.getBobSpkId())
                .bobOpkId(msg.getBobOpkId())
                .timestamp(timestamp)
                .build()
    }

    static OutputEncryptedMessageDTO getOutputEncryptedMessageDTO(EncryptedMessageDTO msg, UUID messageId, String timestamp) {

        if(msg.isInitial()) {
            return new OutputEncryptedMessageDTO(
                    messageId,
                    true,
                    msg.getFrom(),
                    msg.getTo(),
                    msg.getCipher(),
                    msg.getAliceIdentityPublicKey(),
                    msg.getAliceEphemeralPublicKey(),
                    msg.getBobOpkId(),
                    msg.getBobSpkId(),
                    timestamp
            );
        } else {
            return new OutputEncryptedMessageDTO(
                    messageId,
                    false,
                    msg.getFrom(),
                    msg.getTo(),
                    msg.getCipher(),
                    timestamp
            );
        }
    }

}
