package com.szlazakm.safechat.client.presentation.components.userCreation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun PinScreen(
    onPinEntered: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
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
    onInvalidPin: () -> Unit,
    onUserNotCreated: () -> Unit,
    onUserCreated: () -> Unit,
    viewModel: SignInViewModel,
) {
    var enteredPin by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = enteredPin,
            onValueChange = { enteredPin = it },
            label = { Text("Enter PIN") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = {
            scope.launch {

                if (viewModel.state.value.pin == enteredPin) {

                    loading = true
                    val result = viewModel.saveUser()
                    Log.d("VerifyPinScreen", "User creation result: $result")
                    if (result.first) {
                        onUserCreated()
                    } else {
                        Toast.makeText(
                            context,
                            result.second,
                            Toast.LENGTH_SHORT
                        ).show()

                        onUserNotCreated()
                    }

                } else {

                    Toast.makeText(
                        context,
                        "Invalid PIN",
                        Toast.LENGTH_SHORT
                    ).show()

                    onInvalidPin()
                }

            }

            loading = false
        }
        ) {
            Text(text = "Verify")
        }

        if (loading) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Creating user..."
            )
            CircularProgressIndicator()
        }
    }
}