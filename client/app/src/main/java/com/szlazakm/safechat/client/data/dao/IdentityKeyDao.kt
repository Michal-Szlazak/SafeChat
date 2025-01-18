package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.client.data.entities.IdentityKeyEntity
import retrofit2.http.GET

@Dao
interface IdentityKeyDao {

    @Insert
    fun createIdentityKey(identityKeyEntity: IdentityKeyEntity)

    @Query("SELECT * FROM identity_key WHERE phoneNumber = :phoneNumber")
    fun getIdentityKey(phoneNumber: String): IdentityKeyEntity
}