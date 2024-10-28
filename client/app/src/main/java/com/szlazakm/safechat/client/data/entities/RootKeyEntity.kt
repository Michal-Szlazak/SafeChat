package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "root_key")
data class RootKeyEntity (
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val rootKey: ByteArray
)