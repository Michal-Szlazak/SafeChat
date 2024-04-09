package com.szlazakm.safechat.contacts.presentation.components.chat
import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.szlazakm.safechat.contacts.domain.Contact
import com.szlazakm.safechat.contacts.domain.Message
import com.szlazakm.safechat.contacts.presentation.Events.ChatEvent
import com.szlazakm.safechat.contacts.presentation.States.ChatState
import java.util.Date
import java.util.UUID

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel,
    contact: Contact // Add the contact parameter
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = contact.firstName + " " + contact.lastName) },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        },
        content = {innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // You need to obtain the state from the ViewModel
                val state = viewModel.state.collectAsState().value
                MessageList(messages = state.messages)
                Spacer(modifier = Modifier.weight(1f))
                // Pass the ViewModel's onEvent function to the MessageInput
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

@Preview(name = "Chat Screen Preview")
@Composable
fun ChatScreenPreview() {

    val sender = Contact(
        id = UUID.randomUUID(),
        firstName = "John",
        lastName = "Doe",
        email = "john@example.com",
        phoneNumber = "1234567890",
        photo = null
    )

    val receiver = Contact(
        id = UUID.randomUUID(),
        firstName = "John",
        lastName = "Doe",
        email = "john@example.com",
        phoneNumber = "1234567890",
        photo = null
    )

    val fakeState = ChatState(messages = listOf(
        Message.TextMessage("content", senderId = sender.id, Date())),
        receiver,
        false
    )
}
