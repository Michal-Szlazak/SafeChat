package com.szlazakm.safechat.client.data.repositories

import android.content.Context
import androidx.room.Room
import com.szlazakm.safechat.AppDatabase
import com.szlazakm.safechat.client.data.entities.OPKEntity
import com.szlazakm.safechat.client.data.entities.SPKEntity
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

    fun getSPKById(id: Int): SPKEntity {
        return database.spkDao().getSPKById(id)
    }

    fun deleteOPKsByIds(ids : List<Int>) {
        ids.forEach(database.opkDao()::deleteOPKById)
    }

    fun getOPKById(id: Int): OPKEntity {
        return database.opkDao().getOPKById(id)
    }

    fun createSPK(spkEntity: SPKEntity) {
        database.spkDao().insertSPK(spkEntity)
    }
}