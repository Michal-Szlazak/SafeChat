package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.szlazakm.safechat.client.data.entities.IdentityKeyEntity

@Dao
interface IdentityKeyDao {

    @Insert
    fun createIdentityKey(identityKeyEntity: IdentityKeyEntity)
}