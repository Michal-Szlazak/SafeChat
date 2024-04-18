package com.szlazakm.safechat.client.presentation.components.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.szlazakm.safechat.client.presentation.States.SignInState
import androidx.compose.foundation.text.KeyboardOptions as KeyboardOptions1

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onSignInClick: (phoneExtension: String, phoneNumber: String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var phoneExtension by remember { mutableStateOf("+") }
    var phoneNumber by remember { mutableStateOf("") }

    val context = LocalContext.current
    LaunchedEffect(state.phoneVerifyError) {

        state.phoneVerifyError.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = phoneExtension,
            onValueChange = { phoneExtension = it },
            label = { Text("Phone Extension") },
            keyboardOptions = KeyboardOptions1(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions1(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {onSignInClick(phoneExtension, phoneNumber)}) {

            Text(text = "Continue")
        }
    }
}