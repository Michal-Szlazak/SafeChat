package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.GetMessagesDTO;
import com.szlazakm.chatserver.dtos.MessageAcknowledgementDTO;
import com.szlazakm.chatserver.dtos.EncryptedMessageDTO;
import com.szlazakm.chatserver.dtos.response.MessageSentResponseDto;
import com.szlazakm.chatserver.dtos.response.OutputEncryptedMessageDTO;
import com.szlazakm.chatserver.entities.Message;
import com.szlazakm.chatserver.exceptionHandling.exceptions.UnverifiedUserException;
import com.szlazakm.chatserver.services.MessageService;
import com.szlazakm.chatserver.services.NonceService;
import com.szlazakm.chatserver.utils.AuthHelper;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;
    private final NonceService nonceService;
    private final AuthHelper authHelper;

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageSentResponseDto sendMessage(@RequestBody EncryptedMessageDTO msg){

        boolean isUserVerified = authHelper.verifyUser(msg.getPhoneNumber());

        if(!isUserVerified) {
            throw new UnverifiedUserException();
        }

//        nonceService.handleAuthMessage(
//                msg.getPhoneNumber(),
//                msg.getNonce(),
//                msg.getNonceTimestamp(),
//                msg.getAuthMessageSignature()
//        );

        Instant instant = Instant.now();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(
                new Date(instant.toEpochMilli())
        );

        log.info("Timestamp: " + timestamp);
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
                .ephemeralRatchetKey(msg.getEphemeralRatchetKey())
                .messageIndex(msg.getMessageIndex())
                .lastMessageBatchSize(msg.getLastMessageBatchSize())
                .build();

        UUID messageId = messageService.saveMessage(message);

        OutputEncryptedMessageDTO out;

        Log.d("ChatController", "Encrypted message: " + msg);
        if(msg.isInitial()) {
            out = OutputEncryptedMessageDTO.builder()
                    .id(messageId)
                    .initial(true)
                    .from(msg.getFrom())
                    .to(msg.getTo())
                    .cipher(msg.getCipher())
                    .aliceIdentityPublicKey(msg.getAliceIdentityPublicKey())
                    .aliceEphemeralPublicKey(msg.getAliceEphemeralPublicKey())
                    .bobOpkId(msg.getBobOpkId())
                    .bobSpkId(msg.getBobSpkId())
                    .date(timestamp)
                    .ephemeralRatchetKey(msg.getEphemeralRatchetKey())
                    .messageIndex(msg.getMessageIndex())
                    .lastMessageBatchSize(msg.getLastMessageBatchSize())
                    .build();
        } else {
            out = OutputEncryptedMessageDTO.builder()
                    .id(messageId)
                    .initial(false)
                    .from(msg.getFrom())
                    .to(msg.getTo())
                    .cipher(msg.getCipher())
                    .date(timestamp)
                    .ephemeralRatchetKey(msg.getEphemeralRatchetKey())
                    .messageIndex(msg.getMessageIndex())
                    .lastMessageBatchSize(msg.getLastMessageBatchSize())
                    .build();
        }

        simpMessagingTemplate.convertAndSend(
                "/user/queue/" + msg.getTo(), out);

        return MessageSentResponseDto.builder().timestamp(timestamp).build();
    }

    @PostMapping("/acknowledge")
    public void acknowledgeMessage(@RequestBody MessageAcknowledgementDTO msg) {

        boolean isUserVerified = authHelper.verifyUser(msg.getPhoneNumber());

        if(!isUserVerified) {
            throw new UnverifiedUserException();
        }

        nonceService.handleAuthMessage(
                msg.getPhoneNumber(),
                msg.getNonce(),
                msg.getNonceTimestamp(),
                msg.getAuthMessageSignature()
        );

        messageService.acknowledgeMessage(msg);
    }

    @PostMapping("/newMessages")
    public List<OutputEncryptedMessageDTO> getAllNewMessages(@RequestBody GetMessagesDTO getMessagesDTO) {

        boolean isUserVerified = authHelper.verifyUser(getMessagesDTO.getPhoneNumber());

        if(!isUserVerified) {
            throw new UnverifiedUserException();
        }

        nonceService.handleAuthMessage(
                getMessagesDTO.getPhoneNumber(),
                getMessagesDTO.getNonce(),
                getMessagesDTO.getNonceTimestamp(),
                getMessagesDTO.getAuthMessageSignature()
        );

        return messageService.getAllNewMessages(getMessagesDTO.getPhoneNumber());
    }
}
