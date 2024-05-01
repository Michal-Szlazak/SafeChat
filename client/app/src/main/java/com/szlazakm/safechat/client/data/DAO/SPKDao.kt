package com.szlazakm.safechat.client.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.client.data.Entities.SPKEntity

@Dao
interface SPKDao {

    @Insert
    fun insertSPK(spk: SPKEntity)

    @Query("SELECT * FROM spk_entity")
    fun getAllSPKs() : List<SPKEntity>

    @Query("DELETE FROM spk_entity WHERE id=:id")
    fun deleteSPKById(id: Int)
}