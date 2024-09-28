package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.szlazakm.safechat.client.data.entities.ReceiverChainKeyEntity

@Dao
interface ReceiverChainKeyDao {

    @Insert
    fun insertChainKey(chainKey: ReceiverChainKeyEntity)
}