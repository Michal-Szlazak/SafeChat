package com.szlazakm.safechat.client.presentation.components.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.client.data.Entities.UserEntity
import com.szlazakm.safechat.client.data.Repositories.ContactRepository
import com.szlazakm.safechat.client.data.Repositories.UserRepository
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.presentation.States.SignInState
import com.szlazakm.safechat.utils.auth.PreKeyManager
import com.szlazakm.safechat.webclient.dtos.UserCreateDTO
import com.szlazakm.safechat.webclient.dtos.VerifyPhoneNumberDTO
import com.szlazakm.safechat.webclient.webservices.UserWebService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val contactRepository: ContactRepository,
    private val retrofit: Retrofit,
    private val preKeyManager: PreKeyManager
): ViewModel() {

    private val _state: MutableStateFlow<SignInState> = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()
    private val userWebService: UserWebService = retrofit.create(UserWebService::class.java)


    fun isUserCreated(): Flow<Boolean> = flow {

        val isUserCreated = withContext(Dispatchers.IO) {
            userRepository.isUserCreated()
        }
        emit(isUserCreated)
    }

    suspend fun verifyPhoneNumber(code: String): Boolean {
        val verifyPhoneNumberDTO = VerifyPhoneNumberDTO(code)

        // Return a suspended coroutine, allowing it to be seamlessly integrated into a coroutine scope
        return suspendCancellableCoroutine { continuation ->
            userWebService.verifyPhoneNumber(verifyPhoneNumberDTO).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if(result != null) {
                            continuation.resume(result)
                        } else {
                            continuation.resumeWithException(NullPointerException("Response body is null"))
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        continuation.resumeWithException(Exception("Failed with response code: ${response.code()}, message: $errorBody"))
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
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
            identityKey = keyPair.publicKey.serialize(),
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
                identityKeyPair = keyPair.serialize()
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
            }

        }
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

}