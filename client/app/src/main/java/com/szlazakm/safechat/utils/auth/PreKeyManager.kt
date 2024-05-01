package com.szlazakm.safechat.utils.auth

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.szlazakm.safechat.client.data.Repositories.PreKeyRepository
import com.szlazakm.safechat.client.data.Repositories.UserRepository
import com.szlazakm.safechat.client.data.services.PreKeyService
import com.szlazakm.safechat.webclient.dtos.OPKCreateDTO
import com.szlazakm.safechat.webclient.dtos.OPKsCreateDTO
import com.szlazakm.safechat.webclient.dtos.SPKCreateDTO
import com.szlazakm.safechat.webclient.webservices.PreKeyWebService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.signal.libsignal.protocol.ecc.ECPrivateKey
import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.util.KeyHelper
import java.security.interfaces.ECPublicKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PreKeyManager @Inject constructor(
    private val userRepository: UserRepository,
    private val preKeyService: PreKeyService,
    private val preKeyWebService: PreKeyWebService
) {

    fun checkAndProvideOPK() {

        val localUser = userRepository.getLocalUser()

        if(localUser == null) {
            Log.d(
                "PreKeyManager",
                "Cannot check and provide OPKs - local user is not present."
            )
            return
        }

        try {
            val response = preKeyWebService
                .getUnusedOPKIdsByPhoneNumber(localUser.phoneNumber).execute()

            if(response.isSuccessful) {
                val unusedOPKIds = response.body()

                if(unusedOPKIds == null) {
                    Log.e(
                        "PreKeyManager",
                        "Received unused opks list is null."
                    )
                    return
                }

                /* TODO delete them after a certain time has passed
                 so if a message didn't came yet the key would be still in the DB
                 */
                preKeyService.deleteUsedOPKs(unusedOPKIds)

                val biggestId = preKeyService.getLastBiggestId()
                val generatedOpks = generateOneTimePreKeys(biggestId + 1, 10 - unusedOPKIds.size)
                preKeyService.createNewOPKs(generatedOpks)
                sendNewOpksToServer(localUser.phoneNumber, generatedOpks)

            } else {
                Log.e(
                    "PreKeyManager",
                    "Failed to get opks. Response code: ${response.code()}, message: ${response.raw()}"
                )
                return
            }
        } catch (e: Exception) {
            Log.e(
                "PreKeyManager",
                "Error while trying to get opks: ${e.message}"
            )
            return
        }
    }

    fun setSignedPreKey() {
        val localUser = userRepository.getLocalUser()

        if(localUser == null) {
            Log.d(
                "PreKeyManager",
                "Cannot check and provide OPKs - local user is not present."
            )
            return
        }

        val identityKeyPair = IdentityKeyPair(localUser.identityKeyPair)
        val signedPreKeys = generateSignedPreKeys(identityKeyPair)

        val spkCreateDTO = SPKCreateDTO(
            phoneNumber = localUser.phoneNumber,
            id = signedPreKeys.id,
            signedPreKey = signedPreKeys.keyPair.publicKey.serialize(),
            signature = signedPreKeys.signature,
            timestamp = signedPreKeys.timestamp
        )

        preKeyService.createNewSPK(signedPreKeys)
        Log.d("PreKeyManager", "Signed pre key sent to server: $spkCreateDTO")
        val result = preKeyWebService.addNewSPK(spkCreateDTO).execute()

        if(result.isSuccessful) {
            Log.d(
                "PreKeyManager",
                "Signed pre key sent successfully."
            )
        } else {
            Log.e(
                "PreKeyManager",
                "Failed to send signed pre key. Response code: ${result.code()}, message: ${result.raw()}"
            )
        }
    }

    private fun generateSignedPreKeys(identityKeyPair: IdentityKeyPair) : SignedPreKeyRecord {
        val keyId = Random.nextInt(Int.MAX_VALUE)
        return KeyHelper.generateSignedPreKey(identityKeyPair, keyId)
    }

    private fun generateOneTimePreKeys(from: Int, count: Int): List<PreKeyRecord> {
        return KeyHelper.generatePreKeys(from, count)
    }

    private fun sendNewOpksToServer(phoneNumber: String, newOpks: List<PreKeyRecord>) {

        val opkCreateDTOs = newOpks.map {
            opk -> OPKCreateDTO(
                id = opk.id,
                preKey = opk.keyPair.publicKey.serialize()
            )
        }

        val opksCreateDTO = OPKsCreateDTO(
            phoneNumber = phoneNumber,
            opkCreateDTOs = opkCreateDTOs
        )

        Log.d(
            "PreKeyManager",
            "Sending OPKS to server: $opksCreateDTO"
        )

        val response = preKeyWebService.createOPKs(opksCreateDTO).execute()

        if(response.isSuccessful) {
            Log.d(
                "PreKeyManager",
                "OPKS sent successfully."
            )
        } else {
            Log.e(
                "PreKeyManager",
                "Failed to send OPKS. Response code: ${response.code()}, message: ${response.raw()}"
            )
        }
    }

    fun generateIdentityKeys() : IdentityKeyPair? {
        return KeyHelper.generateIdentityKeyPair()
    }
}