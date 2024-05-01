package com.szlazakm.safechat.client.data.Repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.Entities.OPKEntity
import com.szlazakm.safechat.client.data.Entities.SPKEntity
import java.util.UUID
import javax.inject.Inject


class PreKeyRepository @Inject constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "app-database"
    ).fallbackToDestructiveMigration().build()

    fun createOPK(opkEntity: OPKEntity) {
        database.opkDao().insertOPK(opkEntity)
    }

    fun getAllOPKs() : List<OPKEntity> {
        return database.opkDao().getAllOPKs()
    }

    fun deleteOPKsByIds(ids : List<Int>) {
        ids.forEach(database.opkDao()::deleteOPKById)
    }

    fun createSPK(spkEntity: SPKEntity) {
        database.spkDao().insertSPK(spkEntity)
    }

    fun getAllSPKs(): List<SPKEntity> {
        return database.spkDao().getAllSPKs()
    }

    fun deleteSPKsByIds(ids: List<Int>) {
        ids.forEach(database.spkDao()::deleteSPKById)
    }
}