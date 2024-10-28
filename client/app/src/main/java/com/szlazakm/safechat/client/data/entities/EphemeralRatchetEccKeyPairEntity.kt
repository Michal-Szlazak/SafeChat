package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ephemeral_ratchet_ecc_key_pair",
    foreignKeys = [ForeignKey(
        entity = RootKeyEntity::class,
        parentColumns = ["phoneNumber"],
        childColumns = ["phoneNumber"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class EphemeralRatchetEccKeyPairEntity (
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val publicKey: ByteArray,
    val privateKey: ByteArray
)