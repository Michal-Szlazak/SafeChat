package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.szlazakm.safechat.client.data.entities.EphemeralRatchetEccKeyPairEntity

@Dao
interface EphemeralRatchetEccKeyPairDao {

    @Insert
    fun insertKeyPair(ephemeralRatchetEccKeyPairEntity: EphemeralRatchetEccKeyPairEntity)

    @Update
    fun updateKeyPair(ephemeralRatchetEccKeyPairEntity: EphemeralRatchetEccKeyPairEntity)

    @Query("SELECT * FROM ephemeral_ratchet_ecc_key_pair WHERE phoneNumber = :phoneNumber")
    fun getKeyPair(phoneNumber: String): EphemeralRatchetEccKeyPairEntity
}