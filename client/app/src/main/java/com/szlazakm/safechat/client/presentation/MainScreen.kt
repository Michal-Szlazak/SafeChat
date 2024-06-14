package com.szlazakm.safechat.client.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.szlazakm.safechat.client.presentation.components.auth.PinScreen
import com.szlazakm.safechat.client.presentation.components.auth.SignInScreen
import com.szlazakm.safechat.client.presentation.components.auth.SignInUserDetailsScreen
import com.szlazakm.safechat.client.presentation.components.auth.SignInViewModel
import com.szlazakm.safechat.client.presentation.components.auth.VerifyPhoneNumberScreen
import com.szlazakm.safechat.client.presentation.components.auth.VerifyPinScreen
import com.szlazakm.safechat.client.presentation.components.auth.WelcomeScreen
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

    Scaffold { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = ScreenRoutes.LoadingPage.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(ScreenRoutes.LoadingPage.route) {
                WelcomeScreen(
                    viewModel = signInViewModel,
                    navController = navController
                )
            }
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
                            signInViewModel.saveUser(
                                navController = navController,
                                successDestination = ScreenRoutes.ContactList,
                                failureDestination = ScreenRoutes.SignInUserDetails
                            )
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

                PinScreen(
                    onPinEntered = {
                        signInViewModel.savePin(it)
                        navController.navigate(ScreenRoutes.VerifyPin.route)
                    }
                )
            }
            composable(ScreenRoutes.VerifyPin.route) {
                val state by signInViewModel.state.collectAsState()

                VerifyPinScreen(
                    onVerify = { verifyPin ->
                        if(verifyPin == state.pin) {
                            navController.navigate(ScreenRoutes.SignIn.route)
                        } else {
                            navController.navigate(ScreenRoutes.SignInPin.route)
                        }
                    }
                )
            }
            composable(ScreenRoutes.ContactList.route) {
                contactListViewModel.loadContactList()
                ContactListScreen(
                    navController = navController,
                    viewModel = contactListViewModel,
                    chatViewModel = chatViewModel
                )
            }
            composable(
                route = ScreenRoutes.AddContact.route
            ) {

                AddContactScreen(
                    navController = navController,
                    viewModel = addContactViewModel,
                    chatViewModel = chatViewModel
                )
            }
            composable(
                route = ScreenRoutes.Chat.route,
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
