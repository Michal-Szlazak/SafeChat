package com.szlazakm.safechat.client.presentation.components.chat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.szlazakm.safechat.client.presentation.events.ChatEvent

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onInfoButtonClicked: () -> Unit
) {

    val selectedContact = viewModel.state.collectAsState().value.selectedContact
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    selectedContact?.let {
                        Text(text = "${it.firstName} ${it.lastName}")
                    }
                },
                backgroundColor = MaterialTheme.colors.primarySurface,
                actions = {
                    IconButton(onClick = {
                        onInfoButtonClicked()
                        println("Info button clicked!")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color.White // Adjust color if needed
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Message List (scrollable)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp), // Reserve space for the input field
                    reverseLayout = true // Ensures new messages appear at the bottom
                ) {
                    items(state.messages) { message ->
                        MessageListItem(message = message)
                    }
                }

                // Message Input (pinned at the bottom)
                MessageInput(
                    onSend = { message ->
                        viewModel.onEvent(ChatEvent.SendMessage(message))
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun MessageInput(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .padding(8.dp)
            .background(MaterialTheme.colors.surface)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
}
