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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.szlazakm.safechat.client.presentation.Events.ContactListEvent
import com.szlazakm.safechat.client.presentation.ScreenRoutes

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
                                ScreenRoutes.Chat.route.replace("{phoneNumber}", contact.phoneNumber)
                            )
                        }
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}
