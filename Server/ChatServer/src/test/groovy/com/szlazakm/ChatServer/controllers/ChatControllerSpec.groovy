package com.szlazakm.ChatServer.controllers

import com.szlazakm.ChatServer.helpers.TestMessageProvider
import com.szlazakm.chatserver.controllers.ChatController
import com.szlazakm.chatserver.dtos.MessageAcknowledgementDTO
import com.szlazakm.chatserver.services.MessageService
import org.springframework.messaging.simp.SimpMessagingTemplate
import spock.lang.Specification

class ChatControllerSpec extends Specification{

    def simpMessagingTemplate = Mock(SimpMessagingTemplate)
    def messagingService = Mock(MessageService)
    def chatController = new ChatController(simpMessagingTemplate, messagingService)

    def "should call message acknowledge on POST request"() {

        given:
        def uuid = UUID.randomUUID()
        def messageAcknowledgement = MessageAcknowledgementDTO.builder()
            .messageId(uuid)
            .build()

        when:
        chatController.acknowledgeMessage(messageAcknowledgement)

        then:
        1 * messagingService.acknowledgeMessage(messageAcknowledgement)
    }

    def "should return list of OutputEncryptedMessageDTO's"() {

        given:
        def toPhoneNumber = "234234234"
        def messages = TestMessageProvider.messageList
        def returnedMessages = TestMessageProvider.getEncryptedMessageListFromMessageList(messages)

        when:
        def expectedMessages = chatController.getAllNewMessages(toPhoneNumber)

        then:
        1 * messagingService.getAllNewMessages(toPhoneNumber) >> returnedMessages
        expectedMessages == returnedMessages
    }

}
