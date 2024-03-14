package com.szlazakm.safechat.contacts.presentation.components

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.contacts.data.ContactRepository
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.domain.ContactDataSource
import com.szlazakm.safechat.contacts.presentation.ContactListEvent
import com.szlazakm.safechat.contacts.presentation.ContactListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val repository: ContactRepository
): ViewModel() {

    private val _state = MutableStateFlow(ContactListState())
    val state: StateFlow<ContactListState> = _state

    init {
        viewModelScope.launch {
            // Fetch contacts and recent contacts from the repository
            withContext(Dispatchers.IO) {
                repository.insertHardcodedContacts()
            }

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

            ContactListEvent.DismissConversation -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        isSelectedChatSheetOpen = false,
                        isAddChatSheetOpen = false
                    ) }
                    delay(300L) //Delay for animation -> fix
                    _state.update { it.copy(
                        selectedContact = null
                    ) }
                }
            }

            is ContactListEvent.OnConversationClick -> {
                _state.update { it.copy(
                    selectedContact = event.contact,
                    isSelectedChatSheetOpen = true,
                    isAddChatSheetOpen = false
                ) }
                newContact = event.contact
            }

            ContactListEvent.OnNewConversationClick -> {
                _state.update { it.copy(
                    isAddChatSheetOpen = true
                ) }
                newContact = Contact(
                    id = null,
                    firstName = "",
                    lastName = "",
                    email = "",
                    phoneNumber = "",
                    photo = null
                )
            }

            is ContactListEvent.OnFirstNameChanged -> {
                newContact = newContact?.copy(
                    firstName = event.value
                )
            }
            is ContactListEvent.OnLastNameChanged -> {
                newContact = newContact?.copy(
                    lastName = event.value
                )
            }

            ContactListEvent.DismissNewConversation -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        isAddChatSheetOpen = false
                    ) }
                    delay(300L) // Animation delay
                    newContact = null
                    _state.update { it.copy(
                        selectedContact = null
                    ) }
                }
            }
        }
    }
}