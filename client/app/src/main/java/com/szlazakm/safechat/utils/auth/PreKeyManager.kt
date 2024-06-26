package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.data.services.PreKeyService
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.ecc.EccKeyPair
import com.szlazakm.safechat.utils.auth.ecc.EccOpk
import com.szlazakm.safechat.webclient.dtos.OPKCreateDTO
import com.szlazakm.safechat.webclient.dtos.OPKsCreateDTO
import com.szlazakm.safechat.webclient.dtos.SPKCreateDTO
import com.szlazakm.safechat.webclient.webservices.PreKeyWebService
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.DjbECPublicKey
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.util.KeyHelper
import java.util.Base64
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

//        val normalizedIdentityKeyPair = decode(localUser.identityKeyPair)
//        val identityKeyPair = IdentityKeyPair(normalizedIdentityKeyPair)
//        val signedPreKeys = Curve.generateKeyPair()
//        val signature = Curve.calculateSignature(identityKeyPair.privateKey, (signedPreKeys.publicKey as DjbECPublicKey).publicKey)
//        val keyId = Random.nextInt(Int.MAX_VALUE)
//        val publicSignedPrekey = signedPreKeys.publicKey as DjbECPublicKey
//        val timestamp = System.currentTimeMillis()
//        val spkCreateDTO = SPKCreateDTO(
//            phoneNumber = localUser.phoneNumber,
//            id = keyId,
//            signedPreKey = encode(publicSignedPrekey.publicKey),
//            signature = encode(signature),
//            timestamp = timestamp
//        )

        val privateIdentityKey = decode(localUser.privateIdentityKey)
        val signedPreKeys = EccKeyHelper.generateSignedKeyPair(privateIdentityKey)

        val spkCreateDTO = SPKCreateDTO(
            phoneNumber = localUser.phoneNumber,
            id = signedPreKeys.id,
            signedPreKey = encode(signedPreKeys.publicKey),
            signature = encode(signedPreKeys.signature),
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

    private fun generateOneTimePreKeys(from: Int, count: Int): List<EccOpk> {
        return EccKeyHelper.generateOpks(from, count)
    }

    private fun sendNewOpksToServer(phoneNumber: String, newOpks: List<EccOpk>) {

        val opkCreateDTOs = newOpks.map {
            opk -> OPKCreateDTO(
                id = opk.id,
//                preKey = encode((opk.keyPair.publicKey as DjbECPublicKey).publicKey)
                preKey = encode(opk.publicKey)
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

    fun generateIdentityKeys() : EccKeyPair {
        return EccKeyHelper.generateKeyPair()
    }

    private fun encode(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun decode(string: String): ByteArray {
        return Base64.getDecoder().decode(string)
    }
}