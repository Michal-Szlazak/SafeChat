package com.szlazakm.safechat.utils.auth.alice

import android.util.Log
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.ecc.EccKeyPair
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.webclient.dtos.KeyBundleDTO

data class InitializationKeyBundle(
    val bobPublicIdentityKey: ByteArray,
    val bobPublicSignedPreKey: ByteArray,
    val bobPublicOpk: ByteArray? = null,
    val alicePrivateIdentityKey: ByteArray,
    val aliceEphemeralKeyPair: EccKeyPair,
    val ratchetEphemeralKeyPair: EccKeyPair
) {

    companion object {

        fun initializeKeyBundle(keyBundleDTO: KeyBundleDTO, localUser: UserEntity): InitializationKeyBundle {
            val bobIdentityKey = keyBundleDTO.identityKey
            val bobSignedPreKey = keyBundleDTO.signedPreKey
            var bobOpk : ByteArray? = null

            if(keyBundleDTO.onetimePreKey != null) {
                Log.d("EncryptionSessionManager", "Bob's OPK is not null.")
                bobOpk = Decoder.decode(keyBundleDTO.onetimePreKey)
            }

            val aliceEphemeralKeyPair = EccKeyHelper.generateKeyPair()
            val ratchetEphemeralKeyPair = EccKeyHelper.generateKeyPair()
            val alicePrivateIdentityKey = Decoder.decode(localUser.privateIdentityKey)

            return InitializationKeyBundle(
                bobPublicIdentityKey = Decoder.decode(bobIdentityKey),
                bobPublicSignedPreKey = Decoder.decode(bobSignedPreKey),
                bobPublicOpk = bobOpk,
                alicePrivateIdentityKey = alicePrivateIdentityKey,
                aliceEphemeralKeyPair = aliceEphemeralKeyPair,
                ratchetEphemeralKeyPair = ratchetEphemeralKeyPair
            )
        }
    }
}
