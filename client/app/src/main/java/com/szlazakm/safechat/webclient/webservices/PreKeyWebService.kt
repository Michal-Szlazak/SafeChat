package com.szlazakm.safechat.webclient.webservices

import com.szlazakm.safechat.webclient.dtos.GetOpksDTO
import com.szlazakm.safechat.webclient.dtos.OPKsCreateDTO
import com.szlazakm.safechat.webclient.dtos.SPKCreateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PreKeyWebService {

    @POST("/api/opk")
    fun createOPKs(@Body opksCreateDTO: OPKsCreateDTO): Call<Void>

    @POST("/api/opks")
    fun getUnusedOPKIdsByPhoneNumber(@Body getOpksDTO: GetOpksDTO): Call<List<Int>>

    @POST("/api/spk")
    fun  addNewSPK(@Body spkCreateDTO: SPKCreateDTO): Call<Void>
}