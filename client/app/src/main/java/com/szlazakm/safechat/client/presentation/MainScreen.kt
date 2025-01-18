package com.szlazakm.safechat.client.presentation

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.szlazakm.safechat.client.presentation.components.addContact.AddContactScreen
import com.szlazakm.safechat.client.presentation.components.addContact.AddContactViewModel
import com.szlazakm.safechat.client.presentation.components.chat.ChatScreen
import com.szlazakm.safechat.client.presentation.components.chat.ChatVerificationScreen
import com.szlazakm.safechat.client.presentation.components.chat.ChatViewModel
import com.szlazakm.safechat.client.presentation.components.contactList.ContactListScreen
import com.szlazakm.safechat.client.presentation.components.contactList.ContactListViewModel
import com.szlazakm.safechat.client.presentation.components.userCreation.SignInViewModel

fun NavGraphBuilder.mainGraph(
    navController: NavHostController
) {

        navigation(
            startDestination = MainScreenRoutes.ContactList.route,
            route = MainScreenRoutes.MainScreen.route
        ) {
            composable(MainScreenRoutes.ContactList.route) {

                Log.i("MainScreen", "Navigating to contact list")

                val contactListViewModel = hiltViewModel<ContactListViewModel>()
                val parentEntry = remember(navController.currentBackStackEntry) {
                    navController.getBackStackEntry(UserCreationScreenRoutes.MainScreen.route)
                }
                val chatViewModel: ChatViewModel = hiltViewModel(parentEntry)

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

                Log.i("MainScreen", "Navigating to add contact")

                val addContactViewModel = hiltViewModel<AddContactViewModel>()
                addContactViewModel.loadLocalUserData()
                val parentEntry = remember(navController.currentBackStackEntry) {
                    navController.getBackStackEntry(UserCreationScreenRoutes.MainScreen.route)
                }
                val chatViewModel: ChatViewModel = hiltViewModel(parentEntry)

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

                val parentEntry = remember(navController.currentBackStackEntry) {
                    navController.getBackStackEntry(UserCreationScreenRoutes.MainScreen.route)
                }
                val chatViewModel: ChatViewModel = hiltViewModel(parentEntry)

                chatViewModel.loadChat()

                ChatScreen(
                    viewModel = chatViewModel,
                    onInfoButtonClicked = {
                        navController.navigate(MainScreenRoutes.ChatVerification.route)
                    }
                )
            }

            composable(
                route = MainScreenRoutes.ChatVerification.route,
            ) {

                Log.i("MainScreen", "Navigating to chat verification")

                val parentEntry = remember(navController.currentBackStackEntry) {
                    navController.getBackStackEntry(UserCreationScreenRoutes.MainScreen.route)
                }
                val chatViewModel: ChatViewModel = hiltViewModel(parentEntry)

                ChatVerificationScreen(
                    chatViewModel,
                    onScanClicked = {
                        Log.i("MainScreen", "Scan button clicked!")
                    })
            }
        }

}

enum class MainScreenRoutes (val route: String) {
    MainScreen("main"),
    ContactList("contact-list"),
    Chat("chat/{phoneNumber}"),
    AddContact("add-contact"),
    ChatVerification("chat-verification")
}
