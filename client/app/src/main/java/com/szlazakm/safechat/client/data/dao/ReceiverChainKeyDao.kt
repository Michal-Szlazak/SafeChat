package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.szlazakm.safechat.client.data.entities.ReceiverChainKeyEntity
import com.szlazakm.safechat.utils.auth.ecc.ChainKey

@Dao
interface ReceiverChainKeyDao {

    @Insert
    fun insertChainKey(chainKey: ReceiverChainKeyEntity)

    @Update
    fun updateChainKey(chainKey: ReceiverChainKeyEntity)
}