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

    private fun startMessageSaverService(string: String) {
        Log.d("MessageSaverManager", "Starting message saver service from: $string")
        val intent = Intent(context, MessageSaverService::class.java)
        context.startService(intent)
    }

    private fun startOPKSupplierService(string: String) {
        Log.d("MessageSaverManager", "Starting OPK supplier service from: $string")
        val intent = Intent(context, OPKSupplierService::class.java)
        context.startService(intent)
    }

    fun startServices(string: String) {
        startMessageSaverService(string)
        startOPKSupplierService(string)
    }

}