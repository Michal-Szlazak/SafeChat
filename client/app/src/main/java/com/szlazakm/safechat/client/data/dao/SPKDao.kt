package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.client.data.entities.SPKEntity

@Dao
interface SPKDao {

    @Insert
    fun insertSPK(spk: SPKEntity)

    @Query("SELECT * FROM spk_entity")
    fun getAllSPKs() : List<SPKEntity>

    @Query("SELECT * FROM spk_entity WHERE id=:id")
    fun getSPKById(id: Int): SPKEntity

    @Query("DELETE FROM spk_entity WHERE id=:id")
    fun deleteSPKById(id: Int)
}