package com.szlazakm.safechat.contacts.data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_entity")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val createdAt: Date
)