package com.szlazakm.safechat.client.presentation.components.contactList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.client.data.Repositories.ContactRepository
import com.szlazakm.safechat.client.data.Repositories.MessageRepository
import com.szlazakm.safechat.client.data.Repositories.UserRepository
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.presentation.Events.ContactListEvent
import com.szlazakm.safechat.client.presentation.States.ContactListState
import com.szlazakm.safechat.webclient.services.MessageSaverService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val retrofit: Retrofit
): ViewModel() {

    private val _state = MutableStateFlow(ContactListState())
    val state: StateFlow<ContactListState> = _state

    fun clearContacts() {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                contactRepository.clearContacts()
            }
        }
    }

    fun loadContactList() {
        viewModelScope.launch {

            val contacts = withContext(Dispatchers.IO) {
                contactRepository.getContacts()
            }

            _state.value = _state.value.copy(
                contacts = contacts,
            )

        }
    }

    fun loadMessageSaverService() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val messageSaverService = MessageSaverService(
                    contactRepository,
                    messageRepository,
                    userRepository,
                    retrofit
                )
                messageSaverService.connectToUserQueue()
            }
        }
    }

    init {
        loadMessageSaverService()
    }

    var newContact: Contact? by mutableStateOf(null)
        private set

    fun onEvent(event: ContactListEvent) {

        when(event) {

            is ContactListEvent.OnConversationClick -> {
                _state.update { it.copy(
                    selectedContact = event.contact
                ) }
                newContact = event.contact
            }

            ContactListEvent.OnNewConversationClick -> {

                newContact = Contact(
                    firstName = "",
                    lastName = "",
                    phoneNumber = "",
                    photo = null
                )
            }
        }
    }
}