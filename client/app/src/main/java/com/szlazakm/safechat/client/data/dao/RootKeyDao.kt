package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.szlazakm.safechat.client.data.entities.EncryptionSession
import com.szlazakm.safechat.client.data.entities.RootKeyEntity

@Dao
interface RootKeyDao {

    @Insert
    fun insertRootKey(rootKey: RootKeyEntity)

    @Query("SELECT * FROM root_key WHERE phoneNumber = :phoneNumber")
    fun getEncryptionSession(phoneNumber: String): EncryptionSession?

    @Query("SELECT * FROM root_key")
    fun getRootKeys(): List<RootKeyEntity>
    @Update
    fun updateRootKey(rootKeyEntity: RootKeyEntity)
}