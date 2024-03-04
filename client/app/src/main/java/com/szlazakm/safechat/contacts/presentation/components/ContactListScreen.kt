package com.szlazakm.safechat.contacts.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.szlazakm.safechat.contacts.presentation.ContactListEvent
import com.szlazakm.safechat.contacts.presentation.ContactListState

@Composable
fun ContactListScreen(
    state: ContactListState,
    onEvent: (ContactListEvent) -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(ContactListEvent.OnNewConversationClick)
                },
                shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Create,
                        contentDescription = "New Chat"
                    )
                }
        }
    ) {
        innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(state.contacts) { contact ->
                ContactListItem(
                    contact = contact,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onEvent(ContactListEvent.OnConversationClick)
                        }
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}