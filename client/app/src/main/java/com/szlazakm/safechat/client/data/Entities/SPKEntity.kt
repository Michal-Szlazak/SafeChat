package com.szlazakm.safechat.client.data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.PublicKey

@Entity(tableName = "spk_entity")
data class SPKEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val publicKey: ByteArray,
    val privateKey: ByteArray,
    val timestamp: Long
)
