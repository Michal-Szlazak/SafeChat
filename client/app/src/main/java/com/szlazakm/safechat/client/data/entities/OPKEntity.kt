package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "opk_entity")
data class OPKEntity (
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    val privateOPK : String,
    val publicOPK : String,
)