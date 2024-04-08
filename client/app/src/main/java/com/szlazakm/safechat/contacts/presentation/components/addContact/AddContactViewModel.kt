package com.szlazakm.safechat.contacts.presentation.components.addContact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.contacts.data.Repositories.ContactRepository
import com.szlazakm.safechat.contacts.presentation.States.AddContactState
import com.szlazakm.safechat.contacts.presentation.States.ChatState
import com.szlazakm.safechat.webclient.dtos.UserDTO
import com.szlazakm.safechat.webclient.dtos.UserGetDTO
import com.szlazakm.safechat.webclient.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val retrofit: Retrofit
): ViewModel() {

    private val userService: UserService = retrofit.create(UserService::class.java)
    private val _state: MutableStateFlow<AddContactState> = MutableStateFlow(AddContactState(userDTO = null))
    val state: StateFlow<AddContactState> = _state

    fun findUserByPhone(phoneNumber: String) {

        println("Looking for user...")

        viewModelScope.launch {
            val userDto = findUserByPhoneAsync(phoneNumber)
            _state.value = _state.value.copy(
                userDTO = userDto
            )
            println("User found $userDto")
        }
    }

    private suspend fun findUserByPhoneAsync(phoneNumber: String): UserDTO? {
        return withContext(Dispatchers.IO) {

            try {
                val response: Response<UserDTO> = userService.findUserByPhoneNumber(phoneNumber).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                println("Exception ${e.message}")
                null
            }
        }
    }



}
