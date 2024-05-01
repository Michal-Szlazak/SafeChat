package com.szlazakm.safechat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.szlazakm.safechat.client.data.services.MessageSaverService
import com.szlazakm.safechat.client.presentation.SafeChatApp
import com.szlazakm.safechat.ui.theme.SafeChatTheme
import com.szlazakm.safechat.utils.auth.PreKeyManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preKeyManager: PreKeyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "Main activity started")
        super.onCreate(savedInstanceState)
        setContent {
            SafeChatTheme {
                SafeChatApp()
            }
        }

        loadMessageSaverService()

        lifecycleScope.launch(Dispatchers.IO) {
            preKeyManager.checkAndProvideOPK()
        }
    }

    private fun loadMessageSaverService() {
        Log.d("MainActivity", "Loading MessageSaverService")
        val intent = Intent(this, MessageSaverService::class.java)
        this.startService(intent)
        Log.d("MainActivity", "Started MessageSaverService")
    }

}