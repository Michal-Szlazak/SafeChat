package com.szlazakm.safechat.webclient.webservices

import com.szlazakm.safechat.webclient.dtos.OPKCreateDTO
import com.szlazakm.safechat.webclient.dtos.OPKsCreateDTO
import com.szlazakm.safechat.webclient.dtos.SPKCreateDTO
import com.szlazakm.safechat.webclient.dtos.UserCreateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface PreKeyWebService {

    @POST("/api/opk")
    fun createOPKs(@Body opksCreateDTO: OPKsCreateDTO): Call<Void>

    @GET("/api/opk/{phoneNumber}")
    fun getUnusedOPKIdsByPhoneNumber(@Path("phoneNumber") phoneNumber: String): Call<List<Int>>

    @POST("/api/user/spk")
    fun  addNewSPK(@Body spkCreateDTO: SPKCreateDTO): Call<Void>
}