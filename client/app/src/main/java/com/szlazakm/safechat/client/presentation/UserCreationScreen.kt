package com.szlazakm.safechat.client.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.szlazakm.safechat.R
import com.szlazakm.safechat.client.presentation.components.userCreation.PinScreen
import com.szlazakm.safechat.client.presentation.components.userCreation.SignInScreen
import com.szlazakm.safechat.client.presentation.components.userCreation.SignInUserDetailsScreen
import com.szlazakm.safechat.client.presentation.components.userCreation.SignInViewModel
import com.szlazakm.safechat.client.presentation.components.userCreation.VerifyPhoneNumberScreen
import com.szlazakm.safechat.client.presentation.components.userCreation.VerifyPinScreen


fun NavGraphBuilder.userCreationGraph(
    navController: NavHostController
) {

    navigation(
        startDestination = UserCreationScreenRoutes.MainScreen.route,
        route = UserCreationScreenRoutes.UserCreation.route
    ) {
        composable(UserCreationScreenRoutes.SignIn.route) {
            Log.i("UserCreationScreen", "Navigating to sign in")
            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(UserCreationScreenRoutes.SignIn.route)
            }

            // Retrieve the ViewModel scoped to the parent graph
            val vm: SignInViewModel = hiltViewModel(parentEntry)

            SignInScreen(
                viewModel = vm,
                onSignInClick = { phoneExtension: String, phoneNumber: String ->
                    // TODO add phone extension to the phone number
                    vm.setPhoneNumber(phoneNumber)
                    navController.navigate(UserCreationScreenRoutes.SignInUserDetails.route)
                }
            )
        }
        composable(UserCreationScreenRoutes.SignInUserDetails.route) {

            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(UserCreationScreenRoutes.SignIn.route)
            }

            // Retrieve the ViewModel scoped to the parent graph
            val vm: SignInViewModel = hiltViewModel(parentEntry)

            SignInUserDetailsScreen(
                onSaveClicked = { firstName: String, lastName: String ->

                    vm.setUserDetails(
                        firstName,
                        lastName
                    )
                    navController.navigate(UserCreationScreenRoutes.SignInPin.route)
                }
            )
        }
        composable(UserCreationScreenRoutes.SignInPin.route) {

            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(UserCreationScreenRoutes.SignIn.route)
            }

            // Retrieve the ViewModel scoped to the parent graph
            val vm: SignInViewModel = hiltViewModel(parentEntry)

            PinScreen(
                onPinEntered = {
                    vm.savePin(it)
                    navController.navigate(UserCreationScreenRoutes.VerifyPin.route)
                }
            )
        }
        composable(UserCreationScreenRoutes.VerifyPin.route) {

            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(UserCreationScreenRoutes.SignIn.route)
            }

            // Retrieve the ViewModel scoped to the parent graph
            val vm: SignInViewModel = hiltViewModel(parentEntry)
            val state by vm.state.collectAsState()

            VerifyPinScreen(
                viewModel = vm,
                onInvalidPin = {
                    navController.navigate(UserCreationScreenRoutes.SignInPin.route)
                },
                onUserNotCreated = {
                    navController.navigate(UserCreationScreenRoutes.SignIn.route)
                },
                onUserCreated = {
                    navController.navigate(UserCreationScreenRoutes.VerifyPhoneNumber.route)
                }
            )
        }
         composable(UserCreationScreenRoutes.VerifyPhoneNumber.route) {

             val parentEntry = remember(navController.currentBackStackEntry) {
                 navController.getBackStackEntry(UserCreationScreenRoutes.SignIn.route)
             }
             val vm: SignInViewModel = hiltViewModel(parentEntry)

             VerifyPhoneNumberScreen(
                 viewModel = vm,
                 onVerifyClick = {
                     if(it) {
                         vm.saveUserKeys()
                         navController.navigate(MainScreenRoutes.ContactList.route) {
                                launchSingleTop = true
                         }
                     }
                 }
             )
         }
        composable(UserCreationScreenRoutes.MainScreen.route) {
            navController.navigate(MainScreenRoutes.MainScreen.route) {
                popUpTo(UserCreationScreenRoutes.UserCreation.route)
                launchSingleTop = true
            }
        }
    }
}

enum class UserCreationScreenRoutes(val route: String) {
    UserCreation("user-creation"),
    SignIn("sing-in"),
    VerifyPhoneNumber("verify-phone-number"),
    SignInUserDetails("user-details"),
    SignInPin("pin"),
    VerifyPin("verify-pin"),
    MainScreen("main")
}