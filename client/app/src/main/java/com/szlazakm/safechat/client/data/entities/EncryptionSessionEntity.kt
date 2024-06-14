package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encryption_session")
data class EncryptionSessionEntity(
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val symmetricKey: String,
    val ad: String
)
