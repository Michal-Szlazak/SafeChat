package com.szlazakm.safechat.contacts.presentation.components.addContact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.szlazakm.safechat.contacts.presentation.ScreenRoutes
import com.szlazakm.safechat.webclient.dtos.UserDTO
import kotlinx.coroutines.coroutineScope

@Composable
fun AddContactScreen(
    navController: NavController,
    viewModel: AddContactViewModel
) {
    var phoneNumber by remember { mutableStateOf("") }
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = 4.dp
            )
        }
    ) {
        padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Add text field for search
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Button(
                onClick = {
                    viewModel.findUserByPhone(phoneNumber)
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Search")
            }

            // Add space
            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Display user information if found
            state.userDTO?.let { user ->
                UserInfoBubble(
                    user = user,
                    onClick = {

                        viewModel.createContactIfNotExists(state.userDTO)

                        navController.navigate(
                            ScreenRoutes.Chat.route.replace(
                                "{phoneNumber}",
                                state.userDTO.phoneNumber
                            )
                        )
                    }
                )
            }


        }
    }
}

@Composable
fun UserInfoBubble(user: UserDTO, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Name: ${user.firstName} ${user.lastName}",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Phone Number: ${user.phoneNumber}")
        }
    }
}