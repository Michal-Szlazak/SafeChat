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

}
