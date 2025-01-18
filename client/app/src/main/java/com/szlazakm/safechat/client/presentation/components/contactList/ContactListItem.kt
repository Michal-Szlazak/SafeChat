package com.szlazakm.safechat.client.presentation.components.contactList

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.szlazakm.safechat.client.domain.Contact
import com.szlazakm.safechat.client.domain.LocalUserData

@Composable
fun ContactListItem(
    contact: Contact,
    modifier: Modifier = Modifier,
    localUserPhoneNumber: String
) {

    val text = if (contact.phoneNumber == localUserPhoneNumber) {
        "${contact.firstName} ${contact.lastName} (You)"
    } else {
        "${contact.firstName} ${contact.lastName}"
    }
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){

        ContactPhoto(
            contact = contact,
            modifier = Modifier.size(50.dp)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = text,
            modifier = Modifier.weight(1f)
        )
    }
}