package com.szlazakm.safechat.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.szlazakm.safechat.client.data.entities.OPKEntity

@Dao
interface OPKDao {

    @Insert
    fun insertOPK(opk: OPKEntity)

    @Query("SELECT * FROM opk_entity")
    fun getAllOPKs() : List<OPKEntity>

    @Query("DELETE FROM opk_entity WHERE id=:id")
    fun deleteOPKById(id: Int)

    @Query("SELECT * FROM opk_entity WHERE id=:id")
    fun getOPKById(id: Int) : OPKEntity
}