package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.MessageAcknowledgementDTO;
import com.szlazakm.chatserver.dtos.OutputEncryptedMessageDTO;
import com.szlazakm.chatserver.entities.Message;
import com.szlazakm.chatserver.repositories.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @InjectMocks
    private MessageService messageService;

    private Message message;

    @BeforeEach
    void setUp() {

        message = Message.builder()
                .messageId(UUID.randomUUID())
                .aliceIdentityPublicKey("alice-key")
                .cipher("cipher")
                .isInitial(true)
                .bobOpkId(1)
                .bobSpkId(1)
                .toPhoneNumber("123")
                .fromPhoneNumber("321")
                .build();

    }

    @Test
    void saveMessage_ShouldSaveMessage_WhenMessageProvided() {

        // Arrange
        when(messageRepository.save(message)).thenReturn(message);

        // Act
        UUID resultMessageId = messageService.saveMessage(message);

        // Assert
        assertEquals(message.getMessageId(), resultMessageId);
    }

    @Test
    void acknowledgeMessage_ShouldDeleteMessage_WhenMessageAcknowledgementProvided() {

        // Arrange
        UUID messageId = UUID.randomUUID();
        MessageAcknowledgementDTO messageAcknowledgementDTO = MessageAcknowledgementDTO.builder()
                .messageId(messageId)
                .build();

        // Act
        messageService.acknowledgeMessage(messageAcknowledgementDTO);

        // Assert
        verify(messageRepository, times(1)).deleteById(messageId);
    }

    @Test
    void getAllNewMessages_ShouldReturnNewMessages_WhenNewMessagesExist() {

        // Arrange
        String toPhoneNumber = "123";

        Message message1 = Message.builder()
                .messageId(UUID.randomUUID())
                .aliceIdentityPublicKey(null)
                .aliceEphemeralPublicKey(null)
                .cipher("cipher")
                .isInitial(true)
                .bobOpkId(null)
                .bobSpkId(null)
                .toPhoneNumber(toPhoneNumber)
                .fromPhoneNumber("321")
                .timestamp("timestamp-1")
                .build();

        Message message2 = Message.builder()
                .messageId(UUID.randomUUID())
                .aliceIdentityPublicKey(null)
                .aliceEphemeralPublicKey(null)
                .cipher("cipher")
                .isInitial(true)
                .bobOpkId(null)
                .bobSpkId(null)
                .toPhoneNumber(toPhoneNumber)
                .fromPhoneNumber("321")
                .timestamp("timestamp-2")
                .build();

        List<Message> messages = List.of(message1, message2);

        when(messageRepository.getAllByToPhoneNumber(toPhoneNumber)).thenReturn(messages);

        // Act
        List<OutputEncryptedMessageDTO> resultOutputEncryptedMessageDTOS = messageService.getAllNewMessages(toPhoneNumber);

        // Assert
        assertEquals(2, resultOutputEncryptedMessageDTOS.size());
        verify(messageRepository, times(1)).getAllByToPhoneNumber(toPhoneNumber);
        verify(messageRepository, times(1)).deleteById(message1.getMessageId());
        verify(messageRepository, times(1)).deleteById(message2.getMessageId());

        OutputEncryptedMessageDTO resultMessage1 = resultOutputEncryptedMessageDTOS.get(0);
        assertEquals(message1.getMessageId(), resultMessage1.getId());
        assertEquals(message1.getAliceEphemeralPublicKey(), resultMessage1.getAliceEphemeralPublicKey());
        assertEquals(message1.getBobSpkId(), resultMessage1.getBobSpkId());
        assertEquals(message1.getBobOpkId(), resultMessage1.getBobOpkId());
        assertEquals(message1.getCipher(), resultMessage1.getCipher());
        assertEquals(message1.getFromPhoneNumber(), resultMessage1.getFrom());
        assertEquals(message1.getToPhoneNumber(), resultMessage1.getTo());
        assertEquals(message1.isInitial(), resultMessage1.isInitial());

        OutputEncryptedMessageDTO resultMessage2 = resultOutputEncryptedMessageDTOS.get(1);
        assertEquals(message2.getMessageId(), resultMessage2.getId());
        assertEquals(message2.getAliceEphemeralPublicKey(), resultMessage2.getAliceEphemeralPublicKey());
        assertEquals(message2.getBobSpkId(), resultMessage2.getBobSpkId());
        assertEquals(message2.getBobOpkId(), resultMessage2.getBobOpkId());
        assertEquals(message2.getCipher(), resultMessage2.getCipher());
        assertEquals(message2.getFromPhoneNumber(), resultMessage2.getFrom());
        assertEquals(message2.getToPhoneNumber(), resultMessage2.getTo());
        assertEquals(message2.isInitial(), resultMessage2.isInitial());
    }
}