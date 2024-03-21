package com.szlazakm.safechat.contacts.presentation.components.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.szlazakm.safechat.contacts.presentation.States.SignInState

@Composable
fun SignInUserDetailsScreen(
    state: SignInState,
    viewModel: SignInViewModel,
    onSaveClicked: (firstName: String, lastName: String) -> Unit
) {
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            placeholder = { Text("First Name") },
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            placeholder = { Text("Last Name") },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(onClick = {
            // Pass the entered first name and last name to the callback
            onSaveClicked(firstName, lastName)
        }) {
            Text(text = "Continue")
        }
    }
}