package com.szlazakm.safechat.contacts.presentation.components.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.contacts.data.Entities.UserEntity
import com.szlazakm.safechat.contacts.data.Repositories.UserRepository
import com.szlazakm.safechat.contacts.presentation.States.SignInState
import com.szlazakm.safechat.utils.auth.generateKeyPair
import com.szlazakm.safechat.webclient.dtos.UserCreateDTO
import com.szlazakm.safechat.webclient.dtos.VerifyPhoneNumberDTO
import com.szlazakm.safechat.webclient.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val retrofit: Retrofit
): ViewModel() {

    private val _state: MutableStateFlow<SignInState> = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()
    private val userService: UserService = retrofit.create(UserService::class.java)

    fun deleteUser() {

        viewModelScope.launch(Dispatchers.IO) {
            userRepository.clearUserDB()
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    fun isUserCreated(): Flow<Boolean> = flow {

        val isUserCreated = withContext(Dispatchers.IO) {
            userRepository.isUserCreated()
        }
        emit(isUserCreated)
    }

    fun verifyPhoneNumber(code: String) {
        val verifyPhoneNumberDTO = VerifyPhoneNumberDTO(code)

        userService.verifyPhoneNumber(verifyPhoneNumberDTO).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if(result != null) {
                        _state.value = _state.value.copy(
                            phoneVerificationResult = result
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {

            }
        })
    }

    fun setUserDetails(firstName: String, lastName: String) {

        _state.value = _state.value.copy(
            firstName = firstName,
            lastName = lastName
        )
    }

    fun saveUser() {

        val userCreateDTO = UserCreateDTO(
            firstName = state.value.firstName,
            lastName = state.value.lastName,
            phoneNumber = state.value.phoneNumber,
            identityKey = generateKeyPair().toString() //TODO: save the keys to db
        )

        println(userCreateDTO.toString())

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    userService.createUser(userCreateDTO).execute()
                }
                if (response.isSuccessful) {
                    println("User created successfully. Response code: ${response.code()}")
                } else {
                    println("Failed to create user. Response code: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Failed to create user: ${e.message}")
            }
        }

        val user = UserEntity(
            phoneNumber = state.value.phoneNumber,
            firstName = state.value.firstName,
            lastName = state.value.lastName,
            createdAt = Date()
        )
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.createUser(user)
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