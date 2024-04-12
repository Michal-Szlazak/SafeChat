package com.szlazakm.safechat.webclient.services

import com.google.gson.Gson
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import com.szlazakm.safechat.webclient.dtos.OutputMessageDTO
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

class StompService(private val serverUrl: String) {

    private var stompClient: StompClient? = null
    private val gson = Gson()

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

    fun subscribeToTopic(topic: String, callback: (String) -> Unit) : Disposable? {
        val result = stompClient?.topic(topic)?.subscribe { specificUserMessage ->
            val payload = specificUserMessage.payload
            println("called subscire callback")
            callback(payload)
        }
        return result
    }

    fun sendMessage(destination: String, message: MessageDTO) {
        val gsonString = gson.toJson(message)
        println("Sending message $gsonString")
        stompClient?.send("/app/room", gsonString)?.subscribe()
    }
}