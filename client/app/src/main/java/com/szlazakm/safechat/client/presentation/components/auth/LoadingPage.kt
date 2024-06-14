package com.szlazakm.safechat.client.presentation.components.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.szlazakm.safechat.client.presentation.ScreenRoutes
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    viewModel: SignInViewModel,
    navController: NavController
) {
    var isLoading by remember { mutableStateOf(true) }
    var localUserPresent by remember { mutableStateOf(false) }

    // Simulate checking if local user is present in the database
    LaunchedEffect(true) {
        localUserPresent = viewModel.isUserCreated()
        isLoading = false
        delay(1000L)

        if(localUserPresent) {
            viewModel.loadLocalUserData()
            navController.navigate(ScreenRoutes.ContactList.route)
        } else {
            navController.navigate(ScreenRoutes.SignInUserDetails.route)
        }
    }

    Scaffold(
        content = {paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = if (localUserPresent) "Welcome back!" else "Welcome, new user!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h4
                    )
                }
            }
        }
    )
}