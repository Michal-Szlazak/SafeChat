package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.EphemeralRatchetEccKeyPairEntity
import javax.inject.Inject

class EphemeralRatchetKeyPairRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun createEphemeralRatchetKeyPair(ephemeralRatchetEccKeyPairEntity: EphemeralRatchetEccKeyPairEntity) {
        database.ephemeralRatchetEccKeyPairDao().insertKeyPair(ephemeralRatchetEccKeyPairEntity)
    }

    fun updateEphemeralRatchetKeyPair(ephemeralRatchetEccKeyPairEntity: EphemeralRatchetEccKeyPairEntity) {
        database.ephemeralRatchetEccKeyPairDao().updateKeyPair(ephemeralRatchetEccKeyPairEntity)
    }

    fun getEphemeralRatchetKeyPair(phoneNumber: String): EphemeralRatchetEccKeyPairEntity {
        return database.ephemeralRatchetEccKeyPairDao().getKeyPair(phoneNumber)
    }
}