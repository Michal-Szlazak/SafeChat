package com.szlazakm.chatserver.controllers;

import com.szlazakm.chatserver.dtos.MessageDTO;
import com.szlazakm.chatserver.dtos.MessageSentResponseDto;
import com.szlazakm.chatserver.dtos.OutputMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class WebsocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageSentResponseDto sendSpecific(@RequestBody MessageDTO msg){

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        OutputMessageDTO out = new OutputMessageDTO(
                msg.getFrom(),
                msg.getTo(),
                msg.getText(),
                timestamp
        );
        simpMessagingTemplate.convertAndSend(
                "/user/queue/" + msg.getTo(), out);
        return MessageSentResponseDto.builder().timestamp(timestamp).build();
    }
}
