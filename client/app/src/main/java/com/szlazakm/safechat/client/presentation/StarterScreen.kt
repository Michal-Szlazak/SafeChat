package com.szlazakm.safechat.client.presentation

import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.szlazakm.safechat.client.presentation.components.starter.StarterViewModel
import com.szlazakm.safechat.client.presentation.components.starter.WelcomeScreen

fun NavGraphBuilder.starterGraph(
    navController: NavHostController
) {

    navigation(
        startDestination = StarterRoutes.LoadingPage.route,
        route = StarterRoutes.Start.route
    )
         {
            composable(StarterRoutes.LoadingPage.route) {

                val vm = hiltViewModel<StarterViewModel>()

                navController.navigate(StarterRoutes.MainScreen.route) {
                    Log.i("StarterGraph", "Navigating to main screen after user creation checked")
                            popUpTo(StarterRoutes.Start.route) { inclusive = true }
                            launchSingleTop = true
                }

                WelcomeScreen(
                    viewModel = vm,
                    onUserCreated = {
                        navController.navigate(StarterRoutes.MainScreen.route) {
                            Log.i("StarterGraph", "Navigating to main screen after user creation checked")
//                            popUpTo(StarterRoutes.Start.route) { inclusive = true }
//                            launchSingleTop = true
                        }
                    },
                    onUserNotCreated = {
                        navController.navigate(StarterRoutes.UserCreation.route) {
                            Log.i("StarterGraph", "Navigating to user creation after user not exists checked")
                            popUpTo(StarterRoutes.Start.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(StarterRoutes.UserCreation.route) {
                navController.navigate(UserCreationScreenRoutes.UserCreation.route) {
                    Log.i("StarterGraph", "Navigating to user creation")
//                    popUpTo(StarterRoutes.Start.route) { inclusive = true }
//                    navController.popBackStack()
//                    launchSingleTop = true
                }
            }
            composable(StarterRoutes.MainScreen.route) {
                navController.navigate(MainScreenRoutes.ContactList.route) {
                    Log.i("StarterGraph", "Navigating to main screen")
//                    popUpTo(StarterRoutes.Start.route) { inclusive = true }
//                    navController.popBackStack()
                    launchSingleTop = true
                }
            }
        }
}

enum class StarterRoutes (val route: String) {
    Start("start"),
    LoadingPage("loading_page"),
    UserCreation("user_creation"),
    MainScreen("main")
}