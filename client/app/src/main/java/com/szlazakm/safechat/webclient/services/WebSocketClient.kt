package com.szlazakm.safechat.webclient.services

import com.google.gson.Gson
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketListenerImpl : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("WebSocket connection opened")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // Handle the received message
        println("Received message: $text")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket connection closed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("WebSocket connection failure: ${t.message}")
    }
}

fun connectWebSocket(): WebSocket {
    val client = OkHttpClient.Builder().build()

    val request = Request.Builder()
        .url("ws://192.168.0.230:8080/app/room") // WebSocket endpoint URL with user-specific identifier
        .build()

    val listener = WebSocketListenerImpl()

    return client.newWebSocket(request, listener)
}

fun sendMessage(webSocket: WebSocket, message: MessageDTO) {

    val gson = Gson()
    val jsonMessage = gson.toJson(message)
    webSocket.send(jsonMessage)
}