package com.szlazakm.safechat.contacts.presentation.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szlazakm.safechat.contacts.domain.Message

@Composable
fun MessageListItem(
    viewModel: ChatViewModel,
    message: Message.TextMessage
) {
    val isLocalUserMessage = message.senderPhoneNumber == viewModel.localUserEntity.value!!.phoneNumber

    val backgroundColor = if (isLocalUserMessage) {
        Color.Gray // Background color for local user's messages
    } else {
        Color.LightGray // Background color for other users' messages
    }

    val textColor = if (isLocalUserMessage) {
        Color.White // Text color for local user's messages
    } else {
        Color.Black // Text color for other users' messages
    }

    val alignment = if (isLocalUserMessage) {
        TextAlign.End // Align local user's messages to the right
    } else {
        TextAlign.Start // Align other users' messages to the left
    }

    val shape = if (isLocalUserMessage) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 0.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    } else {
        RoundedCornerShape(topStart = 0.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 1.dp),
            contentAlignment = if (isLocalUserMessage) Alignment.CenterEnd else Alignment.CenterStart
        ) {

            Box(
                modifier = Modifier
                    .wrapContentWidth(align = if (isLocalUserMessage) Alignment.End else Alignment.Start)
                    .background(color = backgroundColor, shape = shape)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = if (isLocalUserMessage) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Text(
                    text = message.content,
                    color = textColor,
                    textAlign = alignment,
                    fontSize = 18.sp
                )
            }
        }
    }
}