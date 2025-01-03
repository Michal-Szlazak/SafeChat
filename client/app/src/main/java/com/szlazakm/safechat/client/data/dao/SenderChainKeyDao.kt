package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.szlazakm.safechat.client.data.entities.SenderChainKeyEntity

@Dao
interface SenderChainKeyDao {

    @Insert
    fun insertChainKey(chainKey: SenderChainKeyEntity)

    @Update
    fun updateChainKey(senderChainKeyEntity: SenderChainKeyEntity)
}