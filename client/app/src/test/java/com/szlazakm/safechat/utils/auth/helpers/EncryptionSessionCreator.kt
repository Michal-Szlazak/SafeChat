package com.szlazakm.safechat.utils.auth.helpers

import com.szlazakm.safechat.client.data.entities.EncryptionSession
import com.szlazakm.safechat.client.data.entities.EphemeralRatchetEccKeyPairEntity
import com.szlazakm.safechat.client.data.entities.IdentityKeyEntity
import com.szlazakm.safechat.client.data.entities.ReceiverChainKeyEntity
import com.szlazakm.safechat.client.data.entities.RootKeyEntity
import com.szlazakm.safechat.client.data.entities.SenderChainKeyEntity
import com.szlazakm.safechat.utils.auth.alice.AliceEncryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.alice.InitialMessageEncryptionBundle
import com.szlazakm.safechat.utils.auth.bob.BobDecryptionSessionInitializer
import com.szlazakm.safechat.utils.auth.bob.InitialMessageDecryptionBundle
import com.szlazakm.safechat.utils.auth.ecc.ChainKey
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.ecc.EccKeyPair
import com.szlazakm.safechat.utils.auth.ecc.ReceiverChainKey
import com.szlazakm.safechat.utils.auth.ecc.RootKey
import com.szlazakm.safechat.utils.auth.utils.DiffieHellman

class EncryptionSessionCreator {

    companion object {

        fun createKeyBundles(): Pair<KeyBundle, KeyBundle> {

            val bobKeyBundle =  KeyBundle(
                identityKeyPair = EccKeyHelper.generateKeyPair(),
                signedKeyPair = EccKeyHelper.generateKeyPair(),
                opkKeyPair = EccKeyHelper.generateKeyPair(),
                ephemeralKeyPair = EccKeyHelper.generateKeyPair(),
                ratchetEccKeyPair = EccKeyHelper.generateKeyPair(),
                senderRatchetChain = null,
                receiverRatchetChain = null
            )

            val aliceKeyBundle =  KeyBundle(
                identityKeyPair = EccKeyHelper.generateKeyPair(),
                signedKeyPair = EccKeyHelper.generateKeyPair(),
                opkKeyPair = EccKeyHelper.generateKeyPair(),
                ephemeralKeyPair = EccKeyHelper.generateKeyPair(),
                ratchetEccKeyPair = EccKeyHelper.generateKeyPair(),
                senderRatchetChain = null,
                receiverRatchetChain = null
            )

            val aliceMasterSecret = createAliceMasterSecret(aliceKeyBundle, bobKeyBundle)
            val bobMasterSecret = createBobMasterSecret(aliceKeyBundle, bobKeyBundle)

            val aliceRootKey = RootKey(aliceMasterSecret)

            val aliceKeyPair = aliceRootKey.createChain(
                bobKeyBundle.signedKeyPair.publicKey,
                aliceKeyBundle.ratchetEccKeyPair.privateKey
            )

            val bobRootKey = RootKey(bobMasterSecret)

            val receiverKeyPair = bobRootKey.createChain(
                aliceKeyBundle.ratchetEccKeyPair.publicKey,
                bobKeyBundle.signedKeyPair.privateKey
            )

            val bobSenderKeyPair = receiverKeyPair.first.createChain(
                aliceKeyBundle.ratchetEccKeyPair.publicKey,
                bobKeyBundle.ratchetEccKeyPair.privateKey
            )

            val receiverChainKey = ReceiverChainKey(
                key = receiverKeyPair.second.key,
                index = receiverKeyPair.second.index,
                publicEphemeralKey = aliceKeyBundle.ratchetEccKeyPair.publicKey
            )

            aliceKeyBundle.senderRatchetChain = aliceKeyPair
            bobKeyBundle.senderRatchetChain = bobSenderKeyPair
            bobKeyBundle.receiverRatchetChain = Pair(receiverKeyPair.first, receiverChainKey)

            return Pair(aliceKeyBundle, bobKeyBundle)
        }

        fun createEncryptionSessions(
            alicePhoneNumber: String, bobPhoneNumber: String,
            aliceKeyBundle: KeyBundle, bobKeyBundle: KeyBundle
        ): Pair<EncryptionSession, EncryptionSession> {

            val aliceMasterSecret = createAliceMasterSecret(aliceKeyBundle, bobKeyBundle)
            val bobMasterSecret = createBobMasterSecret(aliceKeyBundle, bobKeyBundle)

            val aliceRootKey = RootKey(aliceMasterSecret)

            val aliceKeyPair = aliceRootKey.createChain(
                bobKeyBundle.signedKeyPair.publicKey,
                aliceKeyBundle.ratchetEccKeyPair.privateKey
            )

            val aliceEncryptionSession = EncryptionSession(
                rootKeyEntity = rootKeyToEntity(bobPhoneNumber, aliceKeyPair.first),
                identityKeyEntity = identityKeyToEntity(bobPhoneNumber, bobKeyBundle.identityKeyPair.publicKey),
                senderChainKeyEntity = chainKeyToSenderChainKeyEntity(bobPhoneNumber, aliceKeyPair.second),
                receiverChainKeyEntities = ArrayList(),
                ephemeralRatchetEccKeyPairEntity = keyPairToRatchetKeyEntity(bobPhoneNumber, aliceKeyBundle.ratchetEccKeyPair)
            )

            val bobRootKey = RootKey(bobMasterSecret)

            val receiverKeyPair = bobRootKey.createChain(
                aliceKeyBundle.ratchetEccKeyPair.publicKey,
                bobKeyBundle.signedKeyPair.privateKey
            )

            val bobSenderKeyPair = receiverKeyPair.first.createChain(
                aliceKeyBundle.ratchetEccKeyPair.publicKey,
                bobKeyBundle.ratchetEccKeyPair.privateKey
            )

            val bobMessageKeys = receiverKeyPair.second.getMessageKeys()

            val receiverChainKeyEntity = ReceiverChainKeyEntity(
                phoneNumber = alicePhoneNumber,
                chainKey = receiverKeyPair.second.key,
                chainKeyIndex = 0,
                publicEphemeralKey = aliceKeyBundle.ratchetEccKeyPair.publicKey
            )

            val bobEncryptionSession = EncryptionSession(
                rootKeyEntity = rootKeyToEntity(alicePhoneNumber, receiverKeyPair.first),
                identityKeyEntity = identityKeyToEntity(alicePhoneNumber, aliceKeyBundle.identityKeyPair.publicKey),
                senderChainKeyEntity = chainKeyToSenderChainKeyEntity(alicePhoneNumber, bobSenderKeyPair.second),
                receiverChainKeyEntities = arrayListOf(receiverChainKeyEntity),
                ephemeralRatchetEccKeyPairEntity = keyPairToRatchetKeyEntity(alicePhoneNumber, bobKeyBundle.ratchetEccKeyPair)
            )

            return Pair(aliceEncryptionSession, bobEncryptionSession)
        }

        private fun rootKeyToEntity(phoneNumber: String, rootKey: RootKey): RootKeyEntity {
            return RootKeyEntity(
                phoneNumber = phoneNumber,
                rootKey = rootKey.key
            )
        }

        private fun identityKeyToEntity(phoneNumber: String, publicKey: ByteArray): IdentityKeyEntity {
            return IdentityKeyEntity(
                phoneNumber = phoneNumber,
                publicKey = publicKey
            )
        }

        private fun chainKeyToSenderChainKeyEntity(phoneNumber: String, chainKey: ChainKey): SenderChainKeyEntity {
            return SenderChainKeyEntity(
                chainKey = chainKey.key,
                chainKeyIndex = chainKey.index,
                phoneNumber = phoneNumber,
                lastMessageBatchSize = 0
            )
        }

        private fun keyPairToRatchetKeyEntity(phoneNumber: String, keyPair: EccKeyPair): EphemeralRatchetEccKeyPairEntity {
            return EphemeralRatchetEccKeyPairEntity(
                phoneNumber = phoneNumber,
                publicKey = keyPair.publicKey,
                privateKey = keyPair.privateKey
            )
        }

        private fun createAliceMasterSecret(aliceKeyBundle: KeyBundle, bobKeyBundle: KeyBundle): ByteArray {

            val bobIdentityKey = bobKeyBundle.identityKeyPair.publicKey
            val bobSignedPreKey = bobKeyBundle.signedKeyPair.publicKey
            val bobOpk = bobKeyBundle.opkKeyPair.publicKey
            val alicePrivateIdentityKey = aliceKeyBundle.identityKeyPair.privateKey
            val aliceEphemeralPrivateKey = aliceKeyBundle.ephemeralKeyPair.privateKey

            val dh1 = DiffieHellman.createSharedSecret(alicePrivateIdentityKey, bobSignedPreKey)
            val dh2 = DiffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobIdentityKey)
            val dh3 = DiffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobSignedPreKey)
            val dh4 = DiffieHellman.createSharedSecret(aliceEphemeralPrivateKey, bobOpk)

            return dh1 + dh2 + dh3 + dh4
        }

        private fun createBobMasterSecret(aliceKeyBundle: KeyBundle, bobKeyBundle: KeyBundle) : ByteArray {

            val aliceIdentityKey = aliceKeyBundle.identityKeyPair.publicKey
            val aliceEphemeralKey = aliceKeyBundle.ephemeralKeyPair.publicKey
            val bobOpk = bobKeyBundle.opkKeyPair.privateKey
            val bobSpk = bobKeyBundle.signedKeyPair.privateKey
            val bobPrivateIdentityKey = bobKeyBundle.identityKeyPair.privateKey

            val dh1 = DiffieHellman.createSharedSecret(bobSpk, aliceIdentityKey)
            val dh2 = DiffieHellman.createSharedSecret(bobPrivateIdentityKey, aliceEphemeralKey)
            val dh3 = DiffieHellman.createSharedSecret(bobSpk, aliceEphemeralKey)
            val dh4 = DiffieHellman.createSharedSecret(bobOpk, aliceEphemeralKey)

            return dh1 + dh2 + dh3 + dh4
        }

        fun encryptionSessionFromAliceInitialMessageEncryptionBundle(
            phoneNumber: String,
            initialMessageEncryptionBundle: InitialMessageEncryptionBundle
        ): EncryptionSession {

            val rootKeyEntity = RootKeyEntity(
                phoneNumber,
                initialMessageEncryptionBundle.ratchetSendingChain.first.key
            )

            val senderChainKeyEntity = SenderChainKeyEntity(
                phoneNumber = phoneNumber,
                chainKey = initialMessageEncryptionBundle.ratchetSendingChain.second.key,
                chainKeyIndex = 0,
                lastMessageBatchSize = 0
            )

            val ephemeralRatchetEccKeyPairEntity = EphemeralRatchetEccKeyPairEntity(
                phoneNumber = phoneNumber,
                publicKey = initialMessageEncryptionBundle.aliceEphemeralRatchetEccKeyPair.publicKey,
                privateKey = initialMessageEncryptionBundle.aliceEphemeralRatchetEccKeyPair.privateKey
            )

            val identityKeyEntity = IdentityKeyEntity(
                phoneNumber = phoneNumber,
                publicKey = initialMessageEncryptionBundle.bobPublicIdentityKey
            )

            return EncryptionSession(
                rootKeyEntity = rootKeyEntity,
                identityKeyEntity = identityKeyEntity,
                senderChainKeyEntity = senderChainKeyEntity,
                receiverChainKeyEntities = ArrayList(),
                ephemeralRatchetEccKeyPairEntity = ephemeralRatchetEccKeyPairEntity
            )
        }

        fun encryptionSessionFromBobInitialMessageDecryptionBundle(
            phoneNumber: String,
            initialMessageDecryptionBundle: InitialMessageDecryptionBundle
        ): EncryptionSession {
            val rootKeyEntity = RootKeyEntity(
                phoneNumber,
                initialMessageDecryptionBundle.ratchetKeyPair.first.key
            )

            val senderChainKeyEntity = SenderChainKeyEntity(
                phoneNumber = phoneNumber,
                chainKey = initialMessageDecryptionBundle.ratchetKeyPair.second.key,
                chainKeyIndex = 0,
                lastMessageBatchSize = 0
            )

            val receiverChainKeyEntity = ReceiverChainKeyEntity(
                phoneNumber = phoneNumber,
                chainKey = initialMessageDecryptionBundle.receiverChainKey.key,
                publicEphemeralKey = initialMessageDecryptionBundle.receiverChainKey.publicEphemeralKey,
                chainKeyIndex = initialMessageDecryptionBundle.receiverChainKey.index
            )

            val identityKeyEntity = IdentityKeyEntity(
                phoneNumber = phoneNumber,
                publicKey = initialMessageDecryptionBundle.theirIdentityPublicKey
            )

            val ephemeralRatchetEccKeyPairEntity  = EphemeralRatchetEccKeyPairEntity(
                phoneNumber = phoneNumber,
                publicKey = initialMessageDecryptionBundle.bobEphemeralRatchetEccKeyPair.publicKey,
                privateKey = initialMessageDecryptionBundle.bobEphemeralRatchetEccKeyPair.privateKey
            )

            return EncryptionSession(
                rootKeyEntity = rootKeyEntity,
                identityKeyEntity = identityKeyEntity,
                senderChainKeyEntity = senderChainKeyEntity,
                receiverChainKeyEntities = arrayListOf(receiverChainKeyEntity),
                ephemeralRatchetEccKeyPairEntity = ephemeralRatchetEccKeyPairEntity
            )
        }
    }
}