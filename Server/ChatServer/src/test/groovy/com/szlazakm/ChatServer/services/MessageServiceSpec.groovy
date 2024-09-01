package com.szlazakm.ChatServer.services

import com.szlazakm.ChatServer.helpers.TestMessageProvider
import com.szlazakm.chatserver.dtos.MessageAcknowledgementDTO
import com.szlazakm.chatserver.repositories.MessageRepository
import com.szlazakm.chatserver.services.MessageService
import spock.lang.Specification

class MessageServiceSpec extends Specification{

    def messageRepository = Mock(MessageRepository)
    def messageService = new MessageService(messageRepository)

    def "should save message repository"() {

        given:
        def message = TestMessageProvider.correctMessage

        when:
        def returnedMessageId = messageService.saveMessage(message)

        then:
        1 * messageRepository.save(message) >> message
        message.messageId == returnedMessageId
    }

    def "should delete message when acknowledged"() {

        given:
        def messageId = UUID.randomUUID()
        def messageAcknowledgement = MessageAcknowledgementDTO.builder()
                .messageId(messageId)
                .build()

        when:
        messageService.acknowledgeMessage(messageAcknowledgement)

        then:
        1 * messageRepository.deleteById(messageId)
    }

    def "should delete messages from db"() {

        given:
        def messages = TestMessageProvider.messageList
        messageRepository.getAllByToPhoneNumber(_ as String) >> messages

        when:
        def returnedMessages = messageService.getAllNewMessages("123123123")

        then:
        for(def message : messages) {
            1 * messageRepository.deleteById(message.messageId)
        }
        returnedMessages == TestMessageProvider.getEncryptedMessageListFromMessageList(messages)
    }

}
