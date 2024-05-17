package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encryption_session")
data class EncryptionSessionEntity(
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val rootKey: String,
    val sendingChainKey: String,
    val ourPublicRatchetKey: String,
    val ourPrivateRatchetKey: String,
    val receivingChainKey: String,
    val theirRatchetKey: String,
    var messageNumber: Int,
    val previousChainLength: Int,
    val ad: String
)
