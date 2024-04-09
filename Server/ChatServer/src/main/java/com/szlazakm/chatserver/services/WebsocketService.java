package com.szlazakm.chatserver.services;

import com.szlazakm.chatserver.dtos.MessageDTO;
import com.szlazakm.chatserver.dtos.OutputMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class WebsocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/room")
    public void sendSpecific(
            @Payload MessageDTO msg,
            Principal user,
            @Header("simpSessionId") String sessionId) throws Exception {
        System.out.println("Got message: " + msg.getText());
        System.out.println(user.getName());
        OutputMessageDTO out = new OutputMessageDTO(
                msg.getFrom(),
                msg.getText(),
                new SimpleDateFormat("HH:mm").format(new Date()));
        simpMessagingTemplate.convertAndSendToUser(
                msg.getTo(), "/user/queue/specific-user", out);
    }
}
