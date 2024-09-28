package com.szlazakm.safechat.client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "encryption_session")
data class EncryptionSessionEntity(
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val symmetricKey: String,
    val ad: String, //TODO to be removed, the root key does the same thing

    @Relation(
        parentColumn = "phoneNumber",
        entityColumn = "phoneNumber"
    )
    val rootKeyEntity: RootKeyEntity,

    @Relation(
        parentColumn = "phoneNumber",
        entityColumn = "phoneNumber"
    )
    val senderChainKeyEntities: List<SenderChainKeyEntity>,

    @Relation(
        parentColumn = "phoneNumber",
        entityColumn = "phoneNumber"
    )
    val receiverChainKeyEntities: List<ReceiverChainKeyEntity>
)
