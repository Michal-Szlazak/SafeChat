package com.szlazakm.safechat.client.presentation.components.contactList

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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.szlazakm.safechat.client.data.entities.ContactEntity
import com.szlazakm.safechat.client.data.entities.toContact
import com.szlazakm.safechat.client.presentation.events.ContactListEvent
import com.szlazakm.safechat.client.presentation.ScreenRoutes
import com.szlazakm.safechat.client.presentation.components.chat.ChatViewModel

@Composable
fun ContactListScreen(
    navController: NavController,
    viewModel: ContactListViewModel,
    chatViewModel: ChatViewModel
) {

    val state by viewModel.state.collectAsState()

    val contactsState = remember { mutableStateOf<List<ContactEntity>?>(null) }

    LaunchedEffect(state.contacts) {
        contactsState.value = state.contacts?.value
    }

    val contactEntities = contactsState.value
    val contacts = contactEntities?.map { c -> c.toContact() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(ScreenRoutes.AddContact.route)
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

            item {
                Text(
                    text = "My contacts (${state.contacts?.value?.size})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            if(contacts != null) {
                items(contacts) { contact ->
                    ContactListItem(
                        contact = contact,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onEvent(ContactListEvent.OnConversationClick(contact))
                                chatViewModel.setContact(contact)
                                navController.navigate(
                                    ScreenRoutes.Chat.route.replace("{phoneNumber}", contact.phoneNumber)
                                )
                            }
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
