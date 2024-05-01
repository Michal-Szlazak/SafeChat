package com.szlazakm.safechat.webclient.webservices

import com.szlazakm.safechat.webclient.dtos.MessageAcknowledgementDTO
import com.szlazakm.safechat.webclient.dtos.MessageDTO
import com.szlazakm.safechat.webclient.dtos.MessageSentResponseDTO
import com.szlazakm.safechat.webclient.dtos.OutputMessageDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatWebService {

    @POST("/room")
    fun sendMessage(@Body message: MessageDTO) : Call<MessageSentResponseDTO>

    @POST("/acknowledge")
    fun acknowledge(@Body messageAcknowledgement: MessageAcknowledgementDTO) : Call<Void>

    @GET("/newMessages/{to}")
    fun getNewMessages(@Path("to") to: String): Call<List<OutputMessageDTO>>
}