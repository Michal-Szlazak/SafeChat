package com.szlazakm.safechat.contacts.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.presentation.components.addContact.AddContactScreen
import com.szlazakm.safechat.contacts.presentation.components.addContact.AddContactViewModel
import com.szlazakm.safechat.contacts.presentation.components.chat.ChatScreen
import com.szlazakm.safechat.contacts.presentation.components.chat.ChatViewModel
import com.szlazakm.safechat.contacts.presentation.components.contactList.ContactListScreen
import com.szlazakm.safechat.contacts.presentation.components.contactList.ContactListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SafeChatApp(
    navController: NavHostController = rememberNavController(),
    contactListViewModel: ContactListViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel(),
    addContactViewModel: AddContactViewModel = viewModel()
) {

    Scaffold { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = ScreenRoutes.ContactList.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            composable(ScreenRoutes.ContactList.route) {
                ContactListScreen(navController = navController, viewModel = contactListViewModel)
            }
            composable(
                route = ScreenRoutes.AddContact.route
            ) {

                AddContactScreen(
                    navController = navController,
                    viewModel = addContactViewModel
                )
            }
            composable(
                route = ScreenRoutes.Chat.route,
                arguments = listOf(
                    navArgument("contactId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val contactId = backStackEntry.arguments?.getLong("contactId")

                var contact by remember { mutableStateOf<Contact?>(null) }

                LaunchedEffect(contactId) {
                    contactId?.let { id ->
                        val fetchedContact = withContext(Dispatchers.IO) {
                            chatViewModel.getContact(id)
                        }
                        contact = fetchedContact
                    }
                }

                contact?.let { ChatScreen(navController = navController, viewModel = chatViewModel, contact = it) }
            }
        }
    }
}