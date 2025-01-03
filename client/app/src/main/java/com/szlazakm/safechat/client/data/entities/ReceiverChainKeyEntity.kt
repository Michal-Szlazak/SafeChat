package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "receiver_chain_key",
    foreignKeys = [ForeignKey(
        entity = RootKeyEntity::class,
        parentColumns = ["phoneNumber"],
        childColumns = ["phoneNumber"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class ReceiverChainKeyEntity (
    @PrimaryKey(autoGenerate = false)
    val chainKey: ByteArray,
    val publicEphemeralKey: ByteArray,
    val chainKeyIndex: Int,
    val phoneNumber: String
)