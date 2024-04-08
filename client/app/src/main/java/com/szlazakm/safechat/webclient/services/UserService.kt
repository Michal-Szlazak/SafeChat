package com.szlazakm.safechat.webclient.services

import com.szlazakm.safechat.webclient.dtos.UserCreateDTO
import com.szlazakm.safechat.webclient.dtos.UserDTO
import com.szlazakm.safechat.webclient.dtos.UserGetDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @POST("/api/user")
    fun createUser(@Body userCreateDTO: UserCreateDTO): Call<Void>

    @GET("/api/user")
    fun findUserByPhoneNumber(@Query("phoneNumber") phoneNumber: String): Call<UserDTO>
}