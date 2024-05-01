package com.szlazakm.safechat.webclient.webservices

import com.szlazakm.safechat.webclient.dtos.MessageDTO
import com.szlazakm.safechat.webclient.dtos.MessageSentResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageSenderWebService {

    @POST("/room")
    fun sendMessage(@Body message: MessageDTO) : Call<MessageSentResponseDTO>
}