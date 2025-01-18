package com.szlazakm.safechat.client.presentation.components.contactList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.client.data.entities.ContactEntity
import com.szlazakm.safechat.client.data.repositories.ContactRepository
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.data.services.ContactListener
import com.szlazakm.safechat.client.data.services.MessageSaverService
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.presentation.events.ContactListEvent
import com.szlazakm.safechat.client.presentation.states.ContactListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository
): ViewModel(), ContactListener {

    private val _state = MutableStateFlow(ContactListState())
    val state: StateFlow<ContactListState> = _state

    fun loadContactList() {

        val messageSaverService = MessageSaverService.getInstance()
        messageSaverService?.setContactListener(this)

        viewModelScope.launch {

            (Dispatchers.IO) {

                _state.value = _state.value.copy(
                    contacts = contactRepository.getAllContacts(),
                    localUserPhoneNumber = userRepository.getLocalUser().phoneNumber
                )
            }
        }
    }

    private var newContact: Contact? by mutableStateOf(null)

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
                    photo = null,
                    securityCode = ""
                )
            }
        }
    }

    override fun onNewContact(contact: Contact) {

        _state.update { it.copy(
            contacts = it.contacts + contact
        ) }
    }
}