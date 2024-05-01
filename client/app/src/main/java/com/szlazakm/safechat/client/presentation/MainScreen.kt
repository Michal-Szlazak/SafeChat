package com.szlazakm.safechat.client.presentation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.szlazakm.safechat.client.data.services.MessageSaverService
import com.szlazakm.safechat.client.presentation.components.addContact.AddContactScreen
import com.szlazakm.safechat.client.presentation.components.addContact.AddContactViewModel
import com.szlazakm.safechat.client.presentation.components.auth.PinScreen
import com.szlazakm.safechat.client.presentation.components.auth.SignInScreen
import com.szlazakm.safechat.client.presentation.components.auth.SignInUserDetailsScreen
import com.szlazakm.safechat.client.presentation.components.auth.SignInViewModel
import com.szlazakm.safechat.client.presentation.components.auth.VerifyPhoneNumberScreen
import com.szlazakm.safechat.client.presentation.components.auth.VerifyPinScreen
import com.szlazakm.safechat.client.presentation.components.chat.ChatScreen
import com.szlazakm.safechat.client.presentation.components.chat.ChatViewModel
import com.szlazakm.safechat.client.presentation.components.contactList.ContactListScreen
import com.szlazakm.safechat.client.presentation.components.contactList.ContactListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SafeChatApp(
    signInViewModel: SignInViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    contactListViewModel: ContactListViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel(),
    addContactViewModel: AddContactViewModel = viewModel()
) {

    val userExists by signInViewModel.isUserCreated().collectAsState(initial = false)
    val startDestination = if (userExists) {
        ScreenRoutes.ContactList.route
    } else {
        ScreenRoutes.SignInUserDetails.route
    }

    Scaffold { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(ScreenRoutes.SignIn.route) {

                SignInScreen(
                    viewModel = signInViewModel,
                    onSignInClick = { phoneExtension: String, phoneNumber: String ->
                        // TODO add phone extension to the phone number
                        signInViewModel.setPhoneNumber(phoneNumber)
                        navController.navigate(ScreenRoutes.VerifyPhoneNumber.route)
                    }
                )
            }
            composable(ScreenRoutes.VerifyPhoneNumber.route) {

                val state by signInViewModel.state.collectAsState()

                VerifyPhoneNumberScreen(
                    state = state,
                    viewModel = signInViewModel,
                    onVerifyClick = {
                        //TODO(verify the code)
                        if(it) {
                            signInViewModel.saveUser()

                            contactListViewModel.loadMessageSaverService()

                            navController.navigate(ScreenRoutes.ContactList.route)
                        } else {
                            navController.navigate(ScreenRoutes.SignIn.route)
                        }
                    }
                )
            }
            composable(ScreenRoutes.SignInUserDetails.route) {

                SignInUserDetailsScreen(
                    onSaveClicked = { firstName: String, lastName: String ->

                        signInViewModel.setUserDetails(
                            firstName,
                            lastName
                        )
                        navController.navigate(ScreenRoutes.SignInPin.route)
                    }
                )
            }
            composable(ScreenRoutes.SignInPin.route) {

                val state by signInViewModel.state.collectAsState()

                PinScreen(
                    state = state,
                    viewModel = signInViewModel,
                    onPinEntered = {
                        signInViewModel.savePin(it)
                        navController.navigate(ScreenRoutes.VerifyPin.route)
                    }
                )
            }
            composable(ScreenRoutes.VerifyPin.route) {
                val state by signInViewModel.state.collectAsState()

                VerifyPinScreen(
                    state = state,
                    onVerify = {
                        if(it) {
                            navController.navigate(ScreenRoutes.SignIn.route)
                        } else {
                            navController.navigate(ScreenRoutes.SignInPin.route)
                        }
                    }
                )
            }
            composable(ScreenRoutes.ContactList.route) {
                contactListViewModel.loadContactList()
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
                    navArgument("phoneNumber") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val contactPhoneNumber = backStackEntry.arguments?.getString("phoneNumber")

                LaunchedEffect(contactPhoneNumber) {
                    contactPhoneNumber?.let {
                        val fetchedContact = withContext(Dispatchers.IO) {
                            chatViewModel.getContact(contactPhoneNumber)
                        }
                        chatViewModel.setContact(fetchedContact!!)
                    }
                }

                val currentContact = chatViewModel.contact.value

                LaunchedEffect(currentContact) {
                    currentContact?.let { chatViewModel.loadChat() }
                }

                currentContact?.let { ChatScreen(viewModel = chatViewModel, contact = it) }
            }
        }
    }
}
