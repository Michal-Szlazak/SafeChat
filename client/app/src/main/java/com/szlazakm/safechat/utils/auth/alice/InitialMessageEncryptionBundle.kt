package com.szlazakm.safechat.utils.auth.alice

import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.utils.auth.ecc.ChainKey
import com.szlazakm.safechat.utils.auth.ecc.EccKeyPair
import com.szlazakm.safechat.utils.auth.ecc.RootKey
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.webclient.dtos.KeyBundleDTO

data class InitialMessageEncryptionBundle (
    val alicePublicIdentityKey: ByteArray,
    val aliceEphemeralPublicKey: ByteArray,
    val aliceEphemeralRatchetEccKeyPair: EccKeyPair,
    val bobPublicIdentityKey: ByteArray,
    val bobSignedPreKeyId: Int,
    val bobOpkId: Int?,
    val ratchetSendingChain: Pair<RootKey, ChainKey>
) {

    companion object {

        fun initializeInitialMessageEncryptionBundle(
            keyBundleDTO: KeyBundleDTO,
            localUser: UserEntity,
            initializationKeyBundle: InitializationKeyBundle,
            ratchetSendingChain: Pair<RootKey, ChainKey>
        ): InitialMessageEncryptionBundle {

            val aliceIdentityKey = Decoder.decode(localUser.publicIdentityKey)

            return InitialMessageEncryptionBundle(
                alicePublicIdentityKey = aliceIdentityKey,
                aliceEphemeralPublicKey = initializationKeyBundle.aliceEphemeralKeyPair.publicKey,
                aliceEphemeralRatchetEccKeyPair = initializationKeyBundle.ratchetEphemeralKeyPair,
                bobPublicIdentityKey = Decoder.decode(keyBundleDTO.identityKey),
                bobSignedPreKeyId = keyBundleDTO.signedPreKeyId,
                bobOpkId = keyBundleDTO.onetimePreKeyId,
                ratchetSendingChain = ratchetSendingChain
            )
        }
    }

}