package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.MessageAcknowledgementDTO;
import com.szlazakm.chatserver.dtos.MessageDTO;
import com.szlazakm.chatserver.dtos.MessageSentResponseDto;
import com.szlazakm.chatserver.dtos.OutputMessageDTO;
import com.szlazakm.chatserver.entities.Message;
import com.szlazakm.chatserver.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageSentResponseDto sendSpecific(@RequestBody MessageDTO msg){

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Message message = Message.builder()
                .toPhoneNumber(msg.getTo())
                .fromPhoneNumber(msg.getFrom())
                .text(msg.getText())
                .timestamp(timestamp)
                .build();

        UUID messageId = messageService.saveMessage(message);

        OutputMessageDTO out = new OutputMessageDTO(
                messageId,
                msg.getFrom(),
                msg.getTo(),
                msg.getText(),
                timestamp
        );

        simpMessagingTemplate.convertAndSend(
                "/user/queue/" + msg.getTo(), out);

        return MessageSentResponseDto.builder().timestamp(timestamp).build();
    }

    @PostMapping("/acknowledge")
    public void acknowledgeMessage(MessageAcknowledgementDTO messageAcknowledgementDTO) {

        messageService.acknowledgeMessage(messageAcknowledgementDTO);
    }

    @GetMapping("/newMessages/{to}")
    public List<OutputMessageDTO> getAllNewMessages(@PathVariable("to") String toPhoneNumber) {

        return messageService.getAllNewMessages(toPhoneNumber);
    }
}
