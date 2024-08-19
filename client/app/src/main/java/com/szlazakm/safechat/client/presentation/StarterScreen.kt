package com.szlazakm.safechat.client.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.szlazakm.safechat.client.presentation.components.starter.StarterViewModel
import com.szlazakm.safechat.client.presentation.components.starter.WelcomeScreen

@Composable
fun StarterScreen(
    navController: NavHostController = rememberNavController(),
    starterViewModel: StarterViewModel = viewModel()
) {

    Scaffold { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = StarterRoutes.LoadingPage.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(StarterRoutes.LoadingPage.route) {
                WelcomeScreen(
                    viewModel = starterViewModel,
                    onUserCreated = {
                        navController.navigate(StarterRoutes.MainScreen.route)
                    },
                    onUserNotCreated = {
                        navController.navigate(StarterRoutes.UserCreation.route)
                    }
                )
            }
            composable(StarterRoutes.UserCreation.route) {
                UserCreationScreen()
            }
            composable(StarterRoutes.MainScreen.route) {
                MainScreen()
            }
        }

    }
}

private enum class StarterRoutes (val route: String) {
    LoadingPage("loadingPage"),
    UserCreation("userCreation"),
    MainScreen("main")
}