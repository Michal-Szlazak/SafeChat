package com.szlazakm.safechat.client.presentation.components.userCreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.szlazakm.safechat.client.presentation.states.SignInState
import kotlinx.coroutines.launch

@Composable
fun VerifyPhoneNumberScreen(
    viewModel: SignInViewModel,
    onVerifyClick: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = FocusRequester()

    var loading by remember { mutableStateOf(false) }

    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter Verification Code", modifier = Modifier.padding(bottom = 16.dp))
        TextField(
            value = code,
            onValueChange = { code = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.focusRequester(focusRequester)
        )

        Button(onClick = {
            loading = true
            scope.launch {
                try {
                    val verified = viewModel.verifyPhoneNumber(
                        code = code,
                        phoneNumber = viewModel.state.value.phoneNumber
                    )

                    if(verified) {
                        onVerifyClick(verified)
                    }
                } finally {
                    loading = false
                }
            }
        }) {
            Text(text = "Verify")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        }
    }
}