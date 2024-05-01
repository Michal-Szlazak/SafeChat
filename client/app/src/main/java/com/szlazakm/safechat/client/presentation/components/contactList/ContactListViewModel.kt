package com.szlazakm.safechat.client.presentation.components.contactList

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szlazakm.safechat.client.data.Repositories.ContactRepository
import com.szlazakm.safechat.client.data.services.MessageSaverService
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.presentation.Events.ContactListEvent
import com.szlazakm.safechat.client.presentation.States.ContactListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val context: Context // TODO almost certainly should be removed
): ViewModel() {

    private val _state = MutableStateFlow(ContactListState())
    val state: StateFlow<ContactListState> = _state

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
        val intent = Intent(context, MessageSaverService::class.java)
        context.startService(intent)
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