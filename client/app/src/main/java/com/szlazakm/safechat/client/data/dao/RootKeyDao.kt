package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.szlazakm.safechat.client.data.entities.RootKeyEntity

@Dao
interface RootKeyDao {

    @Insert
    fun insertRootKey(rootKey: RootKeyEntity)
}