package com.szlazakm.safechat.utils.auth

import android.util.Log
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.data.services.PreKeyService
import com.szlazakm.safechat.excpetions.LocalUserNotFoundException
import com.szlazakm.safechat.utils.auth.ecc.AuthMessageHelper
import com.szlazakm.safechat.utils.auth.ecc.EccKeyHelper
import com.szlazakm.safechat.utils.auth.ecc.EccKeyPair
import com.szlazakm.safechat.utils.auth.ecc.EccOpk
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.Encoder
import com.szlazakm.safechat.utils.auth.utils.Helpers
import com.szlazakm.safechat.webclient.dtos.GetOpksDTO
import com.szlazakm.safechat.webclient.dtos.OPKCreateDTO
import com.szlazakm.safechat.webclient.dtos.OPKsCreateDTO
import com.szlazakm.safechat.webclient.dtos.SPKCreateDTO
import com.szlazakm.safechat.webclient.webservices.PreKeyWebService
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Base64
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreKeyManager @Inject constructor(
    private val userRepository: UserRepository,
    private val preKeyService: PreKeyService,
    private val preKeyWebService: PreKeyWebService
) {

    @Synchronized
    fun checkAndProvideOPK() {

        try {
            val localUser = userRepository.getLocalUser()

            val nonce =  AuthMessageHelper.generateNonce()
            val instant = Instant.now().epochSecond.toString()
            val privateKeyBytes = Decoder.decode(userRepository.getLocalUser().privateIdentityKey)
            val dataToSign = nonce.plus(Decoder.decode(instant))
            val signature = AuthMessageHelper.generateSignature(
                privateKeyBytes,
                dataToSign
            )

            val getOpksDTO = GetOpksDTO(
                phoneNumber = localUser.phoneNumber,
                nonce = nonce,
                nonceTimestamp = instant.toLong(),
                authMessageSignature = signature
            )
            val response = preKeyWebService
                .getUnusedOPKIdsByPhoneNumber(getOpksDTO).execute()

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
                    "Failed to get opks. Response code: ${response.code()}, message: ${response.errorBody()?.string()}"
                )
                return
            }

        } catch (e: LocalUserNotFoundException) {

            Log.e(
                "PreKeyManager",
                "Error while trying to get local user: ${e.message}"
            )
            return
        } catch (e: Exception) {

            Log.e(
                "PreKeyManager",
                "Unknown Exception thrown in PreKeyManager: ${e.message}"
            )
            return
        }
    }

    fun setSignedPreKey() {
        val localUser = userRepository.getLocalUser()

        val privateIdentityKey = decode(localUser.privateIdentityKey)
        val signedPreKeys = EccKeyHelper.generateSignedKeyPair(privateIdentityKey)

        val nonce =  AuthMessageHelper.generateNonce()
        val instant = Instant.now().epochSecond.toString()
        val privateKeyBytes = Decoder.decode(userRepository.getLocalUser().privateIdentityKey)
        val dataToSign = nonce.plus(Decoder.decode(instant))
        val signature = AuthMessageHelper.generateSignature(
            privateKeyBytes,
            dataToSign
        )

        val spkCreateDTO = SPKCreateDTO(
            phoneNumber = localUser.phoneNumber,
            id = signedPreKeys.id,
            signedPreKey = encode(signedPreKeys.publicKey),
            signature = encode(signedPreKeys.signature),
            timestamp = signedPreKeys.timestamp,
            nonce = nonce,
            authMessageSignature = signature,
            nonceTimestamp = instant.toLong()
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
                preKey = encode(opk.publicKey)
            )
        }

        val nonce =  AuthMessageHelper.generateNonce()
        val instant = Instant.now().epochSecond.toString()
        val privateKeyBytes = Decoder.decode(userRepository.getLocalUser().privateIdentityKey)
        val dataToSign = nonce.plus(Decoder.decode(instant))
        val dataBase64 = Encoder.encode(dataToSign)
        Log.d("PreKeyManager", "Data to sign: $dataBase64")
        val signature = AuthMessageHelper.generateSignature(
            privateKeyBytes,
            dataToSign
        )

        val opksCreateDTO = OPKsCreateDTO(
            phoneNumber = phoneNumber,
            opkCreateDTOs = opkCreateDTOs,
            nonce = nonce,
            nonceTimestamp = instant.toLong(),
            authMessageSignature = signature,
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

    open fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}