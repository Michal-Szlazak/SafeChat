package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.MessageDTO;
import com.szlazakm.chatserver.dtos.OutputMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class WebsocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/room")
    public void sendSpecific(@Payload MessageDTO msg){

        OutputMessageDTO out = new OutputMessageDTO(
                msg.getFrom(),
                msg.getTo(),
                msg.getText(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
        );
        simpMessagingTemplate.convertAndSend(
                "/user/queue/" + msg.getTo(), out);
    }
}
