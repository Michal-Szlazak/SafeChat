package com.szlazakm.safechat.contacts.presentation.components.contactList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.contacts.data.Repositories.ContactRepository
import com.szlazakm.safechat.contacts.data.Repositories.UserRepository
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.presentation.Events.ContactListEvent
import com.szlazakm.safechat.contacts.presentation.States.ContactListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val repository: ContactRepository
): ViewModel() {

    private val _state = MutableStateFlow(ContactListState())
    val state: StateFlow<ContactListState> = _state

    fun clearContacts() {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                repository.clearContacts()
            }
        }
    }

    init {
        viewModelScope.launch {
            // Fetch contacts and recent contacts from the repository
//            withContext(Dispatchers.IO) {
//                repository.insertHardcodedContacts()
//            }

            val contacts = withContext(Dispatchers.IO) {
                repository.getContacts()
            }
            val recentContacts = withContext(Dispatchers.IO) {
                repository.getRecentContacts()
            }

            // Update the state flow with the fetched data
            _state.value = _state.value.copy(
                contacts = contacts,
                recentlyActiveContacts = recentContacts
            )

        }
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
                    email = "",
                    phoneNumber = "",
                    photo = null
                )
            }
        }
    }
}