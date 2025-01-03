package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.MessageAcknowledgementDTO;
import com.szlazakm.chatserver.dtos.response.OutputEncryptedMessageDTO;
import com.szlazakm.chatserver.entities.Message;
import com.szlazakm.chatserver.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public UUID saveMessage(Message message) {
        Message savedMessage = messageRepository.save(message);
        return savedMessage.getMessageId();
    }

    public void acknowledgeMessage(MessageAcknowledgementDTO messageAcknowledgementDTO) {

        UUID messageId = messageAcknowledgementDTO.getMessageId();
        messageRepository.deleteById(messageId);
    }

    public List<OutputEncryptedMessageDTO> getAllNewMessages(String toPhoneNumber) {

        List<Message> newMessages = messageRepository.getAllByToPhoneNumber(toPhoneNumber);

        newMessages.forEach(
                message -> messageRepository.deleteById(message.getMessageId())
        );

        return (List<OutputEncryptedMessageDTO>) newMessages.stream().map(
                message -> OutputEncryptedMessageDTO.builder()
                        .id(message.getMessageId())
                        .initial(message.isInitial())
                        .from(message.getFromPhoneNumber())
                        .to(message.getToPhoneNumber())
                        .cipher(message.getCipher())
                        .aliceIdentityPublicKey(message.getAliceIdentityPublicKey())
                        .aliceEphemeralPublicKey(message.getAliceEphemeralPublicKey())
                        .bobOpkId(message.getBobOpkId())
                        .bobSpkId(message.getBobSpkId())
                        .date(message.getTimestamp())
                        .ephemeralRatchetKey(message.getEphemeralRatchetKey())
                        .messageIndex(message.getMessageIndex())
                        .lastMessageBatchSize(message.getLastMessageBatchSize())
                        .build()
        ).toList();
    }
}
