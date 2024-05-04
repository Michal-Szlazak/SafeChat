package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spk_entity")
data class SPKEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val publicKey: String,
    val privateKey: String,
    val timestamp: Long
)
