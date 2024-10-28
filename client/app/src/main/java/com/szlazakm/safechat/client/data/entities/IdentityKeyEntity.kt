package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(
    tableName = "identity_key",
    foreignKeys = [ForeignKey(
        entity = RootKeyEntity::class,
        parentColumns = ["phoneNumber"],
        childColumns = ["phoneNumber"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class IdentityKeyEntity (
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val publicKey: ByteArray
)