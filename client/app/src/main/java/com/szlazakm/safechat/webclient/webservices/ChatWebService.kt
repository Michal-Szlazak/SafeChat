package com.szlazakm.safechat.webclient.webservices

import com.szlazakm.safechat.webclient.dtos.EncryptedMessageDTO
import com.szlazakm.safechat.webclient.dtos.GetMessagesDTO
import com.szlazakm.safechat.webclient.dtos.MessageAcknowledgementDTO
import com.szlazakm.safechat.webclient.dtos.MessageSentResponseDTO
import com.szlazakm.safechat.webclient.dtos.OutputEncryptedMessageDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatWebService {

    @POST("/room")
    fun sendMessage(@Body message: EncryptedMessageDTO) : Call<MessageSentResponseDTO>

    @POST("/acknowledge")
    fun acknowledge(@Body messageAcknowledgement: MessageAcknowledgementDTO) : Call<Void>

    @POST("/newMessages")
    fun getNewMessages(@Body getMessagesDTO: GetMessagesDTO): Call<List<OutputEncryptedMessageDTO>>
}