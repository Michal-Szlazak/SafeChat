package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.MessageAcknowledgementDTO;
import com.szlazakm.chatserver.dtos.OutputMessageDTO;
import com.szlazakm.chatserver.entities.Message;
import com.szlazakm.chatserver.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public List<OutputMessageDTO> getAllNewMessages(String toPhoneNumber) {

        List<Message> newMessages = messageRepository.getAllByToPhoneNumber(toPhoneNumber);

        newMessages.forEach(
                message -> messageRepository.deleteById(message.getMessageId())
        );

        return newMessages.stream().map(
                message -> new OutputMessageDTO(
                        message.getMessageId(),
                        message.getFromPhoneNumber(),
                        message.getToPhoneNumber(),
                        message.getText(),
                        message.getTimestamp()
                )
        ).toList();
    }
}
