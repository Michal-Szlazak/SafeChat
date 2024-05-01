package com.szlazakm.safechat.client.data.services

import com.szlazakm.safechat.client.data.Entities.OPKEntity
import com.szlazakm.safechat.client.data.Entities.SPKEntity
import com.szlazakm.safechat.client.data.Repositories.PreKeyRepository
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import kotlin.streams.toList

class PreKeyService constructor(
    val preKeyRepository: PreKeyRepository
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

        if (opks.isNotEmpty()) {
            return opks.maxByOrNull { it.id }?.id ?: 0

        } else {
            return 0
        }
    }

    fun createNewOPKs(newOpks: List<PreKeyRecord>) {

        val opkEntities = newOpks.map{
            opk -> OPKEntity(
                id = opk.id,
                publicOPK = opk.keyPair.publicKey.serialize(),
                privateOPK = opk.keyPair.privateKey.serialize()
            )
        }

        opkEntities.forEach(preKeyRepository::createOPK)
    }

    fun createNewSPK(signedPreKey: SignedPreKeyRecord) {

        val spkEntity = SPKEntity(
            id = signedPreKey.id,
            publicKey = signedPreKey.keyPair.publicKey.serialize(),
            privateKey = signedPreKey.keyPair.privateKey.serialize(),
            timestamp = signedPreKey.timestamp
        )

        preKeyRepository.createSPK(spkEntity)
    }

}