package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chain_key")
class SenderChainKeyEntity (
    @PrimaryKey(autoGenerate = false)
    val chainKey: String,
    val chainKeyIndex: Int,
    val phoneNumber: String,
    val timestamp: Long
)