package com.szlazakm.safechat.webclient.services

import com.google.gson.Gson
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

class StompService(private val serverUrl: String) {

    private var stompClient: StompClient? = null

    fun connect() {
        try {
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, serverUrl)
            stompClient?.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun disconnect() {
        stompClient?.disconnect()
    }

    fun subscribeToTopic(topic: String, callback: (String) -> Unit) {
        //TODO
    }

    fun sendMessage(destination: String, message: MessageDTO) {
        val gson = Gson()
        val gsonString = gson.toJson(message)
        stompClient?.send("/app/room", gsonString)?.subscribe()
    }
}