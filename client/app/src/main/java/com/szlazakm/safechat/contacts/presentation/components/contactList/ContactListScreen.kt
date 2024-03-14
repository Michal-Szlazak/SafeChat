package com.szlazakm.safechat.contacts.presentation.components.contactList

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.presentation.Events.ContactListEvent
import com.szlazakm.safechat.contacts.presentation.ScreenRoutes
import com.szlazakm.safechat.contacts.presentation.States.ContactListState

@Composable
fun ContactListScreen(
    navController: NavController,
    viewModel: ContactListViewModel
) {

    val state by viewModel.state.collectAsState()

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
                    text = "My contacts (${state.contacts.size})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            items(state.contacts) { contact ->
                ContactListItem(
                    contact = contact,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent(ContactListEvent.OnConversationClick(contact))
                            navController.navigate(
                                ScreenRoutes.Chat.route.replace("{contactId}", contact.id.toString())
                            )
                        }
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "My Composable Preview")
@Composable
fun ContactListScreenPreview() {
    val fakeState = ContactListState(contacts = listOf(
        Contact(
            id = 2,
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            email = "john@example.com",
            photo = null
        ),
        Contact(
            id = 1,
            firstName = "Jane",
            lastName = "Doe",
            phoneNumber = "0987654321",
            email = "jane@example.com",
            photo = null
        )
    ))

    val onEvent: (ContactListEvent) -> Unit = { event ->
        // Do nothing or print the event for testing purposes
        println("Received event: $event")
    }
}