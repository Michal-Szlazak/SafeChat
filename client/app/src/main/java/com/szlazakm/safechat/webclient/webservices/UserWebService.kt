package com.szlazakm.safechat.webclient.webservices

import com.szlazakm.safechat.webclient.dtos.KeyBundleDTO
import com.szlazakm.safechat.webclient.dtos.UserCreateDTO
import com.szlazakm.safechat.webclient.dtos.UserDTO
import com.szlazakm.safechat.webclient.dtos.VerifyPhoneNumberDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserWebService {

    @POST("/api/user")
    fun createUser(@Body userCreateDTO: UserCreateDTO): Call<Void>

    @GET("/api/user")
    fun findUserByPhoneNumber(@Query("phoneNumber") phoneNumber: String): Call<UserDTO>

    @POST("/api/user/verify")
    fun verifyPhoneNumber(@Body verifyPhoneNumberDTO: VerifyPhoneNumberDTO) : Call<Boolean>

    @GET("/api/user/keyBundle")
    fun getKeyBundle(@Query("phoneNumber") phoneNumber: String): Call<KeyBundleDTO>

}