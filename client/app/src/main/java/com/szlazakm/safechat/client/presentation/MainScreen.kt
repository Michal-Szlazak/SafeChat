package com.szlazakm.safechat.client.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.szlazakm.safechat.client.presentation.components.addContact.AddContactScreen
import com.szlazakm.safechat.client.presentation.components.addContact.AddContactViewModel
import com.szlazakm.safechat.client.presentation.components.chat.ChatScreen
import com.szlazakm.safechat.client.presentation.components.chat.ChatViewModel
import com.szlazakm.safechat.client.presentation.components.contactList.ContactListScreen
import com.szlazakm.safechat.client.presentation.components.contactList.ContactListViewModel

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    contactListViewModel: ContactListViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel(),
    addContactViewModel: AddContactViewModel = viewModel()
) {

    Scaffold { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = MainScreenRoutes.ContactList.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(MainScreenRoutes.ContactList.route) {
                contactListViewModel.loadContactList()
                ContactListScreen(
                    navController = navController,
                    viewModel = contactListViewModel,
                    chatViewModel = chatViewModel
                )
            }
            composable(
                route = MainScreenRoutes.AddContact.route
            ) {

                AddContactScreen(
                    navController = navController,
                    viewModel = addContactViewModel,
                    chatViewModel = chatViewModel
                )
            }
            composable(
                route = MainScreenRoutes.Chat.route,
                arguments = listOf(
                    navArgument("phoneNumber") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val contactPhoneNumber = backStackEntry.arguments?.getString("phoneNumber")

                chatViewModel.loadChat()
                ChatScreen(viewModel = chatViewModel)
            }
        }

    }
}

enum class MainScreenRoutes (val route: String) {
    ContactList("contactList"),
    Chat("chat"),
    AddContact("addContact")
}