package com.szlazakm.safechat.client.presentation.components.chat

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.szlazakm.safechat.client.domain.Message

@Composable
fun MessageList(
    viewModel: ChatViewModel,
    messages: List<Message.TextMessage>
) {
    LazyColumn {
        items(messages) { message ->
            MessageListItem(viewModel = viewModel, message = message)
        }
    }
}
