package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.MessageAcknowledgementDTO;
import com.szlazakm.chatserver.dtos.EncryptedMessageDTO;
import com.szlazakm.chatserver.dtos.response.MessageSentResponseDto;
import com.szlazakm.chatserver.dtos.response.OutputEncryptedMessageDTO;
import com.szlazakm.chatserver.entities.Message;
import com.szlazakm.chatserver.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.whispersystems.libsignal.logging.Log;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;
    private final Instant instant;

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageSentResponseDto sendMessage(@RequestBody EncryptedMessageDTO msg){

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                new Date(instant.getEpochSecond() * 1000)
        );

        Message message = Message.builder()
                .isInitial(msg.isInitial())
                .toPhoneNumber(msg.getTo())
                .fromPhoneNumber(msg.getFrom())
                .cipher(msg.getCipher())
                .aliceIdentityPublicKey(msg.getAliceIdentityPublicKey())
                .aliceEphemeralPublicKey(msg.getAliceEphemeralPublicKey())
                .bobSpkId(msg.getBobSpkId())
                .bobOpkId(msg.getBobOpkId())
                .timestamp(timestamp)
                .build();

        UUID messageId = messageService.saveMessage(message);

        OutputEncryptedMessageDTO out;

        Log.d("ChatController", "Encrypted message: " + msg);
        if(msg.isInitial()) {
            out = new OutputEncryptedMessageDTO(
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
            out = new OutputEncryptedMessageDTO(
                    messageId,
                    false,
                    msg.getFrom(),
                    msg.getTo(),
                    msg.getCipher(),
                    timestamp
            );
        }

        simpMessagingTemplate.convertAndSend(
                "/user/queue/" + msg.getTo(), out);

        return MessageSentResponseDto.builder().timestamp(timestamp).build();
    }

    @PostMapping("/acknowledge")
    public void acknowledgeMessage(@RequestBody MessageAcknowledgementDTO messageAcknowledgementDTO) {

        messageService.acknowledgeMessage(messageAcknowledgementDTO);
    }

    @GetMapping("/newMessages/{to}")
    public List<OutputEncryptedMessageDTO> getAllNewMessages(@PathVariable("to") String toPhoneNumber) {

        return messageService.getAllNewMessages(toPhoneNumber);
    }
}
