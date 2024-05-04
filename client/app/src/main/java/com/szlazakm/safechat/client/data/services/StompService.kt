package com.szlazakm.safechat.client.data.services

import android.util.Log
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

class StompService {

    private var stompClient: StompClient? = null
    private val serverUrl = "ws://192.168.0.230:8080/ws"

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
        Log.d("StompService", "Subscribed to topic")
        val result = stompClient?.topic(topic)?.subscribe { specificUserMessage ->
            val payload = specificUserMessage.payload
            callback(payload)
        }
        return result
    }
}