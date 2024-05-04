package com.szlazakm.safechat.client.presentation.components.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.data.services.MessageSaverManager
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.domain.LocalUserData
import com.szlazakm.safechat.client.presentation.States.SignInState
import com.szlazakm.safechat.utils.auth.PreKeyManager
import com.szlazakm.safechat.webclient.dtos.UserCreateDTO
import com.szlazakm.safechat.webclient.dtos.VerifyPhoneNumberDTO
import com.szlazakm.safechat.webclient.webservices.UserWebService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.whispersystems.libsignal.ecc.DjbECPublicKey
import java.util.Base64
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val contactRepository: ContactRepository,
    private val userWebService: UserWebService,
    private val preKeyManager: PreKeyManager,
    private val messageSaverManager: MessageSaverManager
): ViewModel() {

    private val _state: MutableStateFlow<SignInState> = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    suspend fun loadLocalUserData() {
        return (Dispatchers.IO) {
            val user = userRepository.getLocalUser()
            if(user != null) {
                LocalUserData.getInstance().setUserData(user)
            }
        }
    }

    suspend fun isUserCreated(): Boolean {

        return (Dispatchers.IO) {
            userRepository.isUserCreated()
        }
    }

    suspend fun verifyPhoneNumber(code: String): Boolean {
        val verifyPhoneNumberDTO = VerifyPhoneNumberDTO(code)

        return (Dispatchers.IO) {
            userWebService.verifyPhoneNumber(verifyPhoneNumberDTO).execute().isSuccessful
        }
    }

    fun setUserDetails(firstName: String, lastName: String) {

        _state.value = _state.value.copy(
            firstName = firstName,
            lastName = lastName
        )
    }

    fun saveUser() {

        val keyPair = preKeyManager.generateIdentityKeys()

        if(keyPair == null || keyPair.publicKey == null || keyPair.privateKey == null) {
            Log.e("SignInViewModel", "Failed to generate key pair.")
            return
        }

        val userCreateDTO = UserCreateDTO(
            firstName = state.value.firstName,
            lastName = state.value.lastName,
            phoneNumber = state.value.phoneNumber,
            identityKey = encode((keyPair.publicKey.publicKey as DjbECPublicKey).publicKey),
            pin = state.value.pin
        )

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    userWebService.createUser(userCreateDTO).execute()
                }
                if (response.isSuccessful) {
                    println("User created successfully. Response code: ${response.code()}")
                } else {
                    println("Failed to create user. Response code: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Failed to create user: ${e.message}")
            }


            val user = UserEntity(
                phoneNumber = state.value.phoneNumber,
                firstName = state.value.firstName,
                lastName = state.value.lastName,
                createdAt = Date(),
                identityKeyPair = encode(keyPair.serialize())
            )

            val localUserContact = Contact(
                phoneNumber = state.value.phoneNumber,
                firstName = state.value.firstName,
                lastName = state.value.lastName,
                photo = null
            )

            withContext(Dispatchers.IO) {
                userRepository.createUser(user)
                contactRepository.createContact(localUserContact)
                preKeyManager.setSignedPreKey()
                preKeyManager.checkAndProvideOPK()
                loadMessageSaverService()
                loadLocalUserData()
            }

        }
    }

    private fun loadMessageSaverService() {
        messageSaverManager.startMessageSaverService("SignInViewModel")
    }

    fun savePin(pin: String) {

        _state.value = _state.value.copy(
            pin = pin
        )
    }

    fun setPhoneNumber(phoneNumber: String) {
        _state.value = _state.value.copy(
            phoneNumber = phoneNumber
        )
    }

    private fun encode(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

}