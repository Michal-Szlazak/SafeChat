package com.szlazakm.safechat.client.presentation.components.contactList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.client.data.repositories.ContactRepository
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
    private val contactRepository: ContactRepository
): ViewModel() {

    private val _state = MutableStateFlow(ContactListState())
    val state: StateFlow<ContactListState> = _state


    fun loadContactList() {
        viewModelScope.launch {

            (Dispatchers.IO) {

                _state.value = _state.value.copy(
                    contacts = contactRepository.getAllContacts()
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
                    photo = null
                )
            }
        }
    }
}