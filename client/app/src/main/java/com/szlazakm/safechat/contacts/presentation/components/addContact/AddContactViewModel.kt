package com.szlazakm.safechat.contacts.presentation.components.addContact

import androidx.lifecycle.ViewModel
import com.szlazakm.safechat.contacts.data.Repositories.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val repository: ContactRepository
): ViewModel() {

}
