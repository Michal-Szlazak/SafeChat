package com.szlazakm.safechat.client.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.szlazakm.safechat.client.presentation.components.userCreation.PinScreen
import com.szlazakm.safechat.client.presentation.components.userCreation.SignInScreen
import com.szlazakm.safechat.client.presentation.components.userCreation.SignInUserDetailsScreen
import com.szlazakm.safechat.client.presentation.components.userCreation.SignInViewModel
import com.szlazakm.safechat.client.presentation.components.userCreation.VerifyPhoneNumberScreen
import com.szlazakm.safechat.client.presentation.components.userCreation.VerifyPinScreen

@Composable
fun UserCreationScreen(
    signInViewModel: SignInViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    Scaffold { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = UserCreationScreenRoutes.SignIn.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(UserCreationScreenRoutes.SignIn.route) {

                SignInScreen(
                    viewModel = signInViewModel,
                    onSignInClick = { phoneExtension: String, phoneNumber: String ->
                        // TODO add phone extension to the phone number
                        signInViewModel.setPhoneNumber(phoneNumber)
                        navController.navigate(UserCreationScreenRoutes.VerifyPhoneNumber.route)
                    }
                )
            }
            composable(UserCreationScreenRoutes.VerifyPhoneNumber.route) {

                val state by signInViewModel.state.collectAsState()

                VerifyPhoneNumberScreen(
                    state = state,
                    viewModel = signInViewModel,
                    onVerifyClick = {
                        //TODO(verify the code)
                        if(it) {
                            signInViewModel.saveUser(
                                navController = navController,
                                successDestination = UserCreationScreenRoutes.MainScreen,
                                failureDestination = UserCreationScreenRoutes.SignInUserDetails
                            )
                            navController.navigate(UserCreationScreenRoutes.MainScreen.route)
                        } else {
                            navController.navigate(UserCreationScreenRoutes.SignIn.route)
                        }
                    }
                )
            }
            composable(UserCreationScreenRoutes.SignInUserDetails.route) {

                SignInUserDetailsScreen(
                    onSaveClicked = { firstName: String, lastName: String ->

                        signInViewModel.setUserDetails(
                            firstName,
                            lastName
                        )
                        navController.navigate(UserCreationScreenRoutes.SignInPin.route)
                    }
                )
            }
            composable(UserCreationScreenRoutes.SignInPin.route) {

                PinScreen(
                    onPinEntered = {
                        signInViewModel.savePin(it)
                        navController.navigate(UserCreationScreenRoutes.VerifyPin.route)
                    }
                )
            }
            composable(UserCreationScreenRoutes.VerifyPin.route) {
                val state by signInViewModel.state.collectAsState()

                VerifyPinScreen(
                    onVerify = { verifyPin ->
                        if(verifyPin == state.pin) {
                            navController.navigate(UserCreationScreenRoutes.SignIn.route)
                        } else {
                            navController.navigate(UserCreationScreenRoutes.SignInPin.route)
                        }
                    }
                )
            }
            composable(UserCreationScreenRoutes.MainScreen.route) {
                MainScreen()
            }
        }
    }
}

enum class UserCreationScreenRoutes(val route: String) {
    SignIn("sing_in"),
    VerifyPhoneNumber("verify_phone_number"),
    SignInUserDetails("user_details"),
    SignInPin("pin"),
    VerifyPin("verify_pin"),
    MainScreen("main")
}