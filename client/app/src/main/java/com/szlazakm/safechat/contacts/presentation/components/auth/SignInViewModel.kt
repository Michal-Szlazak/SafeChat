package com.szlazakm.safechat.contacts.presentation.components.auth

import androidx.lifecycle.ViewModel
import com.szlazakm.safechat.contacts.data.Entities.UserEntity
import com.szlazakm.safechat.contacts.data.Repositories.UserRepository
import com.szlazakm.safechat.contacts.presentation.States.SignInState
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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _state: MutableStateFlow<SignInState> = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun deleteUser() {

        GlobalScope.launch(Dispatchers.IO) {
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

    fun verifyPhoneNumber(code: String): Boolean {
        //TODO(verify the code)
        return true
    }

    fun setUserDetails(firstName: String, lastName: String) {

        _state.value = _state.value.copy(
            firstName = firstName,
            lastName = lastName
        )
    }

    fun saveUser() {

        val user = UserEntity(
            phoneNumber = state.value.phoneNumber,
            firstName = state.value.firstName,
            lastName = state.value.lastName,
            createdAt = Date()
        )
        GlobalScope.launch(Dispatchers.IO) {
            userRepository.createUser(user)
        }
    }

    fun savePin(pin: String) {

        _state.value = _state.value.copy(
            pin = pin
        )
    }

}