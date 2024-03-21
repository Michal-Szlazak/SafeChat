package com.szlazakm.safechat.contacts.presentation.components.auth

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.szlazakm.safechat.contacts.presentation.States.SignInState

@Composable
fun PinScreen(
    state: SignInState,
    viewModel: SignInViewModel,
    onPinEntered: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = pin,
            onValueChange = { pin = it },
            label = { Text("Enter PIN") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = { onPinEntered(pin) }) {
            Text(text = "Submit")
        }
    }
}

@Composable
fun VerifyPinScreen(
    state: SignInState,
    viewModel: SignInViewModel,
    onVerify: (Boolean) -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = enteredPin,
            onValueChange = { enteredPin = it },
            label = { Text("Enter PIN") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = { onVerify(enteredPin == state.pin) }) {
            Text(text = "Verify")
        }
    }
}