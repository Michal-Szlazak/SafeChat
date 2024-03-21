package com.szlazakm.safechat.contacts.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.szlazakm.safechat.contacts.presentation.components.auth.PinScreen
import com.szlazakm.safechat.contacts.presentation.components.auth.SignInScreen
import com.szlazakm.safechat.contacts.presentation.components.auth.SignInUserDetailsScreen
import com.szlazakm.safechat.contacts.presentation.components.auth.SignInViewModel
import com.szlazakm.safechat.contacts.presentation.components.auth.VerifyPhoneNumberScreen
import com.szlazakm.safechat.contacts.presentation.components.auth.VerifyPinScreen
import com.szlazakm.safechat.contacts.presentation.components.chat.ChatScreen
import com.szlazakm.safechat.contacts.presentation.components.chat.ChatViewModel
import com.szlazakm.safechat.contacts.presentation.components.contactList.ContactListScreen
import com.szlazakm.safechat.contacts.presentation.components.contactList.ContactListViewModel
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

    signInViewModel.deleteUser()
    val userExists by signInViewModel.isUserCreated().collectAsState(initial = false)
    val startDestination = if (userExists) {
        ScreenRoutes.ContactList.route
    } else {
        ScreenRoutes.SignIn.route
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

                val state by signInViewModel.state.collectAsState()

                SignInScreen(
                    state= state,
                    viewModel = signInViewModel,
                    onSignInClick = {
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
                        val verified = signInViewModel.verifyPhoneNumber(it)
                        if(verified) {
                            navController.navigate(ScreenRoutes.SignInPin.route)
                        } else {
                            navController.navigate(ScreenRoutes.SignIn.route)
                        }
                    }
                )
            }
            composable(ScreenRoutes.SignInUserDetails.route) {
                val state by signInViewModel.state.collectAsState()

                SignInUserDetailsScreen(
                    state = state,
                    viewModel = signInViewModel,
                    onSaveClicked = { firstName: String, lastName: String ->

                        signInViewModel.setUserDetails(
                            firstName,
                            lastName
                        )
                        signInViewModel.saveUser()
                        navController.navigate(ScreenRoutes.ContactList.route)
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
                    viewModel = signInViewModel,
                    onVerify = {
                        if(it) {
                            navController.navigate(ScreenRoutes.SignInUserDetails.route)
                        } else {
                            navController.navigate(ScreenRoutes.SignInPin.route)
                        }
                    }
                )
            }
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