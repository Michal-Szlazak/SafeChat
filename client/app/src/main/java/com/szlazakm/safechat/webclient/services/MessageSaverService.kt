package com.szlazakm.safechat.webclient.services

import ua.naiksoftware.stomp.Stomp

class MessageSaverService {

    private val stompService: StompService = StompService("ws://192.168.0.230:8080/ws")

    fun connectToUserQueue(phoneNumber: String) {
        stompService.connect()
        stompService.subscribeToTopic("/user/queue/$phoneNumber") {message ->
            println("Received by messageSaverService $message")
        }
    }
}