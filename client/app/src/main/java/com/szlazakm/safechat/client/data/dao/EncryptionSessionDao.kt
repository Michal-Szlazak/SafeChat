package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.client.data.entities.EncryptionSessionEntity

@Dao
interface EncryptionSessionDao {

    @Query("SELECT * FROM encryption_session WHERE phoneNumber=:phoneNumber")
    fun getEncryptionSessionByPhoneNumber(phoneNumber: String) : EncryptionSessionEntity?

    @Query("SELECT * FROM encryption_session")
    fun getAllEncryptionSessions() : List<EncryptionSessionEntity>

    @Query("DELETE FROM encryption_session WHERE phoneNumber=:phoneNumber")
    fun deleteEncryptionSessionByPhoneNumber(phoneNumber: String)

    @Insert
    fun createEncryptionSession(encryptionSessionEntity: EncryptionSessionEntity)
}