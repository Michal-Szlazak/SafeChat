package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.szlazakm.safechat.client.data.entities.SenderChainKeyEntity

@Dao
interface SenderChainKeyDao {

    @Insert
    fun insertChainKey(chainKey: SenderChainKeyEntity)
}