package com.szlazakm.safechat.client.data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.ecc.ECPrivateKey
import java.util.Date

@Entity(tableName = "user_entity")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val createdAt: Date,
    val identityKeyPair: ByteArray
)