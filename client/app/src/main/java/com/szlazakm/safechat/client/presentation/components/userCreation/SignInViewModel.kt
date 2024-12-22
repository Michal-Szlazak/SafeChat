package com.szlazakm.safechat.client.presentation.components.userCreation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.data.services.MessageSaverManager
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.domain.LocalUserData
import com.szlazakm.safechat.client.presentation.MainScreenRoutes
import com.szlazakm.safechat.client.presentation.UserCreationScreenRoutes
import com.szlazakm.safechat.client.presentation.states.SignInState
import com.szlazakm.safechat.utils.auth.PreKeyManager
import com.szlazakm.safechat.utils.auth.ecc.AuthMessageHelper
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.Encoder
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
import java.text.SimpleDateFormat
import java.time.Instant
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
            try {
                val user = userRepository.getLocalUser()
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Error while trying to load local user data: ${e.message}")
            }
        }
    }

    suspend fun verifyPhoneNumber(code: String, phoneNumber: String): Boolean {
        val verifyPhoneNumberDTO = VerifyPhoneNumberDTO(code, phoneNumber)

        return (Dispatchers.IO) {
            try {
                val response = userWebService.verifyPhoneNumber(verifyPhoneNumberDTO).execute()
                if(response.isSuccessful) {
                    Log.d("SignInViewModel", "Response successful. Body: ${response.body()}")
                    response.body()?: false
                } else {
                    Log.e("SignInViewModel", "Response unsuccessful. Code: ${response.code()}")
                    response.body()?: false
                }
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Error while trying to verify phone number: ${e.message}")
                false
            }
        }
    }

    fun setUserDetails(firstName: String, lastName: String) {

        _state.value = _state.value.copy(
            firstName = firstName,
            lastName = lastName
        )
    }

    fun saveUserKeys() {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                preKeyManager.setSignedPreKey()
                preKeyManager.checkAndProvideOPK()
            }
        }
    }

    fun saveUser(
        navController: NavController,
        successDestination: UserCreationScreenRoutes,
        failureDestination: UserCreationScreenRoutes
    ) {

        val keyPair = preKeyManager.generateIdentityKeys()

        viewModelScope.launch {

            try {

                val response = withContext(Dispatchers.IO) {

                    val userCreateDTO = UserCreateDTO(
                        firstName = state.value.firstName,
                        lastName = state.value.lastName,
                        phoneNumber = state.value.phoneNumber,
                        identityKey = encode(keyPair.publicKey),
                        pin = state.value.pin
                    )

                    userWebService.createUser(userCreateDTO).execute()
                }
                if (response.isSuccessful) {
                    Log.d("SignInViewModel", "User created successfully.")

                    val user = UserEntity(
                        phoneNumber = state.value.phoneNumber,
                        firstName = state.value.firstName,
                        lastName = state.value.lastName,
                        createdAt = Date(),
                        publicIdentityKey = encode(keyPair.publicKey),
                        privateIdentityKey = encode(keyPair.privateKey)
                    )

                    LocalUserData.getInstance().setUserData(user)

                    val localUserContact = Contact(
                        phoneNumber = state.value.phoneNumber,
                        firstName = state.value.firstName,
                        lastName = state.value.lastName,
                        photo = null
                    )

                    withContext(Dispatchers.IO) {
                        userRepository.createUser(user)
                        contactRepository.createContact(localUserContact)
//                        preKeyManager.setSignedPreKey()
//                        preKeyManager.checkAndProvideOPK()
                        loadMessageSaverService()
                        loadLocalUserData()
                    }

                    navController.navigate(successDestination.route)

                } else {
                    Log.e("SignInViewModel", "Failed to create user: ${response.code()}, message: ${response.raw()}")
                    navController.navigate(failureDestination.route)
                }
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Error while trying to create user: ${e.message}")
                navController.navigate(failureDestination.route)
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