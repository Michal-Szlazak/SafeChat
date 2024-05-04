package com.szlazakm.safechat.client.data.services

import com.szlazakm.safechat.client.data.entities.OPKEntity
import com.szlazakm.safechat.client.data.entities.SPKEntity
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import org.whispersystems.libsignal.ecc.DjbECPrivateKey
import org.whispersystems.libsignal.ecc.DjbECPublicKey
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import java.util.Base64

class PreKeyService(
    private val preKeyRepository: PreKeyRepository
) {

    fun deleteUsedOPKs(unusedOPKIds: List<Int>) {

        val storedOPKs = preKeyRepository.getAllOPKs()
        val storedOPKIds = storedOPKs.stream().map {
            opk -> opk.id
        }

        val usedOPKIds = storedOPKIds.filter{ !unusedOPKIds.contains(it) }.toList()
        preKeyRepository.deleteOPKsByIds(usedOPKIds)
    }

    fun getLastBiggestId() : Int {
        val opks = preKeyRepository.getAllOPKs()

        return if (opks.isNotEmpty()) {
            opks.maxByOrNull { it.id }?.id ?: 0

        } else {
            0
        }
    }

    fun createNewOPKs(newOpks: List<PreKeyRecord>) {

        val opkEntities = newOpks.map { opk ->
            OPKEntity(
                id = opk.id,
                publicOPK = encode((opk.keyPair.publicKey as DjbECPublicKey).publicKey),
                privateOPK = encode((opk.keyPair.privateKey as DjbECPrivateKey).privateKey)
            )
        }

        opkEntities.forEach(preKeyRepository::createOPK)
    }

    fun createNewSPK(signedPreKey: SignedPreKeyRecord) {

        val publicSignedPreKey = signedPreKey.keyPair.publicKey as DjbECPublicKey
        val privateSignedPreKey = signedPreKey.keyPair.privateKey as DjbECPrivateKey

        val spkEntity = SPKEntity(
            id = signedPreKey.id,
            publicKey = encode(publicSignedPreKey.publicKey),
            privateKey = encode(privateSignedPreKey.privateKey),
            timestamp = signedPreKey.timestamp
        )

        preKeyRepository.createSPK(spkEntity)
    }

    private fun encode(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

}