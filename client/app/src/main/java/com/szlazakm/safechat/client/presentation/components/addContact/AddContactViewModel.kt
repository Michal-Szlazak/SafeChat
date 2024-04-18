package com.szlazakm.safechat.client.presentation.components.addContact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.client.data.Repositories.ContactRepository
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.presentation.States.AddContactState
import com.szlazakm.safechat.webclient.dtos.UserDTO
import com.szlazakm.safechat.webclient.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        viewModelScope.launch {
            val userDto = findUserByPhoneAsync(phoneNumber)
            _state.value = _state.value.copy(
                userDTO = userDto
            )
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

    fun createContactIfNotExists(userDTO: UserDTO) {

        val contact = Contact(
            firstName = userDTO.firstName,
            lastName = userDTO.lastName,
            phoneNumber = userDTO.phoneNumber,
            photo = null
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if(!repository.contactExists(contact.phoneNumber)) {
                    repository.createContact(contact)
                }
            }
        }
    }

}
