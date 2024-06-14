package com.szlazakm.safechat.client.data.services

import android.content.Context
import android.content.Intent
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageSaverManager @Inject constructor(
    private val context: Context
){

    fun startMessageSaverService(string: String) {
        Log.d("MessageSaverManager", "Starting message saver service from: $string")
        val intent = Intent(context, MessageSaverService::class.java)
        context.startService(intent)
    }

}