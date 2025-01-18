package com.szlazakm.safechat.client.data.services

import android.util.Log
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import java.util.concurrent.ConcurrentHashMap

class StompService {

    private var stompClient: StompClient? = null
    private val serverUrl = "ws://192.168.0.230:8080/ws" //FOR PC
//    private val serverUrl = "ws://safechat-986401487521.us-central1.run.app/ws" //For GCP
    private val activeSubscriptions = ConcurrentHashMap<String, Disposable>()

    private fun connect() {
        try {
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, serverUrl)
            stompClient?.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun disconnect() {
        stompClient?.disconnect()
        activeSubscriptions.values.forEach { it.dispose() }
        activeSubscriptions.clear()
    }

    private fun isConnected(): Boolean {
        return stompClient?.isConnected ?: false
    }

    @Synchronized
    fun subscribeToTopic(topic: String, callback: (String) -> Unit) {

        connect()

        if (activeSubscriptions.containsKey(topic)) {
            Log.d("StompService", "Already subscribed to topic: $topic")
            return
        }

        Log.d("StompService", "Subscribed to topic")
        val disposable = stompClient?.topic(topic)?.subscribe(
            { specificUserMessage ->
                val payload = specificUserMessage.payload
                callback(payload)
            },
            { error ->
                Log.e("StompService", "Error subscribing to topic: $topic", error)
                activeSubscriptions.remove(topic) // Remove from active subscriptions on error
            }
        )

        disposable?.let {
            activeSubscriptions[topic] = it
            Log.d("StompService", "Subscribed to topic: $topic")
        }
    }
}