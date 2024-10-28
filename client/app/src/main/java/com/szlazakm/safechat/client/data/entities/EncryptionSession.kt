package com.szlazakm.safechat.client.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class EncryptionSession(

    @Embedded val rootKeyEntity: RootKeyEntity,

    @Relation(
        parentColumn = "phoneNumber",
        entityColumn = "phoneNumber"
    )
    val identityKeyEntity: IdentityKeyEntity,

    @Relation(
        parentColumn = "phoneNumber",
        entityColumn = "phoneNumber"
    )
    val senderChainKeyEntity: SenderChainKeyEntity,

    @Relation(
        parentColumn = "phoneNumber",
        entityColumn = "phoneNumber"
    )
    val receiverChainKeyEntities: List<ReceiverChainKeyEntity>,

    @Relation(
        parentColumn = "phoneNumber",
        entityColumn = "phoneNumber"
    )
    val ephemeralRatchetEccKeyPairEntity: EphemeralRatchetEccKeyPairEntity
) {
    override fun toString(): String {
        return """
            {
                rootKey:            $rootKeyEntity,
                senderChainKey:     $senderChainKeyEntity,
                receiverChainKeys:  $receiverChainKeyEntities
                ephemeralKeyPair:   $ephemeralRatchetEccKeyPairEntity
        """.trimIndent()
    }
}
