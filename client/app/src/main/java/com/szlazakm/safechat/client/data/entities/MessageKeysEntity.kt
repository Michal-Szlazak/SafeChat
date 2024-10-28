package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_keys_entity")
class MessageKeysEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val ephemeralRatchetKey: ByteArray,
    val phoneNumber: String,
    val cipherKey: ByteArray,
    val macKey: ByteArray,
    val iv: ByteArray,
    val index: Int
)