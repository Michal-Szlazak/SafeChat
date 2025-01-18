package com.szlazakm.safechat.client.data.services

import com.szlazakm.safechat.client.data.entities.MessageEntity

interface MessageListener {
    fun onNewMessage(message: MessageEntity)
    fun afterRecovery()
}