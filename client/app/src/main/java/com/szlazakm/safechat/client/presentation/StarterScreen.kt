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

                WelcomeScreen(
                    viewModel = vm,
                    onUserCreated = {
                        navController.navigate(StarterRoutes.MainScreen.route)
                    },
                    onUserNotCreated = {
                        navController.navigate(StarterRoutes.UserCreation.route)
                    }
                )
            }
            composable(StarterRoutes.UserCreation.route) {
                navController.navigate(UserCreationScreenRoutes.UserCreation.route) {
                    popUpTo(StarterRoutes.Start.route)
                    launchSingleTop = true
                }
            }
            composable(StarterRoutes.MainScreen.route) {
                navController.navigate(MainScreenRoutes.ContactList.route) {
//                    popUpTo(StarterRoutes.Start.route)
                    Log.i("StarterGraph", "Navigating to main screen")
                    launchSingleTop = true
                }
            }
        }
}

enum class StarterRoutes (val route: String) {
    Start("start"),
    LoadingPage("loadingPage"),
    UserCreation("userCreation"),
    MainScreen("main")
}