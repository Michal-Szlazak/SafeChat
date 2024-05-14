package com.szlazakm.safechat.client.data.services

import com.szlazakm.safechat.client.data.entities.OPKEntity
import com.szlazakm.safechat.client.data.entities.SPKEntity
import com.szlazakm.safechat.client.data.repositories.PreKeyRepository
import com.szlazakm.safechat.utils.auth.ecc.EccOpk
import com.szlazakm.safechat.utils.auth.ecc.EccSignedKeyPair
import org.whispersystems.libsignal.ecc.DjbECPrivateKey
import org.whispersystems.libsignal.ecc.DjbECPublicKey
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import java.sql.Timestamp
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

    fun createNewOPKs(newOpks: List<EccOpk>) {

        val opkEntities = newOpks.map { opk ->
            OPKEntity(
                id = opk.id,
//                publicOPK = encode((opk.keyPair.publicKey as DjbECPublicKey).publicKey),
//                privateOPK = encode((opk.keyPair.privateKey as DjbECPrivateKey).privateKey)
                publicOPK = encode(opk.publicKey),
                privateOPK = encode(opk.privateKey)
            )
        }

        opkEntities.forEach(preKeyRepository::createOPK)
    }

    fun createNewSPK(spk: EccSignedKeyPair) {

        val spkEntity = SPKEntity(
            id = spk.id,
            publicKey = encode(spk.publicKey),
            privateKey = encode(spk.privateKey),
            timestamp = spk.timestamp
        )

        preKeyRepository.createSPK(spkEntity)
    }

    private fun encode(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

}