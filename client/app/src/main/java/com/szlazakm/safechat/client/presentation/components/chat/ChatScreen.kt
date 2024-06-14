package com.szlazakm.safechat.client.presentation.components.chat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.szlazakm.safechat.client.presentation.events.ChatEvent

@Composable
fun ChatScreen(
    viewModel: ChatViewModel
) {

    val selectedContact = viewModel.state.collectAsState().value.selectedContact

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (selectedContact != null) {
                        Text(text =  selectedContact.firstName + " " + selectedContact.lastName)
                    }
                },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        },
        content = {innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val state = viewModel.state.collectAsState().value
                MessageList(messages = state.messages)
                Spacer(modifier = Modifier.weight(1f))
                MessageInput(onSend = { message ->
                    viewModel.onEvent(ChatEvent.SendMessage(message))
                })
            }
        }
    )
}



@Composable
fun MessageInput(onSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }

    TextField(
        value = message,
        onValueChange = { message = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        label = { Text("Type a message...") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(
            onSend = {
                if (message.isNotBlank()) {
                    onSend(message)
                    message = ""
                }
            }
        )
    )
}
