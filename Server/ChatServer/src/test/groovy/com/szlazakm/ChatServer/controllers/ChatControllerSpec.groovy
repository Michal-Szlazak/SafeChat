package com.szlazakm.ChatServer.controllers

import com.szlazakm.ChatServer.helpers.TestMessageProvider
import com.szlazakm.chatserver.controllers.ChatController
import com.szlazakm.chatserver.dtos.MessageAcknowledgementDTO
import com.szlazakm.chatserver.services.MessageService
import org.springframework.messaging.simp.SimpMessagingTemplate
import spock.lang.Specification

import java.text.SimpleDateFormat
import java.time.Instant

class ChatControllerSpec extends Specification{

    def simpMessagingTemplate = Mock(SimpMessagingTemplate)
    def messageService = Mock(MessageService)
    def instant = Mock(Instant)
    def chatController = new ChatController(simpMessagingTemplate, messageService, instant)

    def "should send message to user topic and return timestamp from server"() {

        given:
        def messageId = UUID.randomUUID()
        def encryptedMessageDTO = TestMessageProvider.getEncryptedMessageDTO()

        def timestampLong = 1726413444L
        def timestampString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                new Date(timestampLong * 1000)
        )

        def messageEntity = TestMessageProvider.getMessageFromEncryptedMessageDTO(encryptedMessageDTO, timestampString)
        def outputMessageDTO = TestMessageProvider.getOutputEncryptedMessageDTO(
                encryptedMessageDTO, messageId, timestampString
        )

        instant.getEpochSecond() >> timestampLong
        1 * messageService.saveMessage(messageEntity) >> messageId
        when:
        def returnedTimestamp = chatController.sendMessage(encryptedMessageDTO)

        then:
        1 * simpMessagingTemplate.convertAndSend("/user/queue/" + encryptedMessageDTO.getTo(), outputMessageDTO)
        returnedTimestamp.timestamp == timestampString
    }

    def "should call message acknowledge on POST request"() {

        given:
        def uuid = UUID.randomUUID()
        def messageAcknowledgement = MessageAcknowledgementDTO.builder()
            .messageId(uuid)
            .build()

        when:
        chatController.acknowledgeMessage(messageAcknowledgement)

        then:
        1 * messageService.acknowledgeMessage(messageAcknowledgement)
    }

    def "should return list of OutputEncryptedMessageDTO's"() {

        given:
        def toPhoneNumber = "234234234"
        def messages = TestMessageProvider.messageList
        def returnedMessages = TestMessageProvider.getEncryptedMessageListFromMessageList(messages)

        when:
        def expectedMessages = chatController.getAllNewMessages(toPhoneNumber)

        then:
        1 * messageService.getAllNewMessages(toPhoneNumber) >> returnedMessages
        expectedMessages == returnedMessages
    }

}
