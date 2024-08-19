package com.szlazakm.safechat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.szlazakm.safechat.client.data.services.MessageSaverManager
import com.szlazakm.safechat.client.presentation.StarterScreen
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

    @Inject
    lateinit var messageSaverManager: MessageSaverManager

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "Main activity started")
        super.onCreate(savedInstanceState)
        setContent {
            SafeChatTheme {
                StarterScreen()
            }
        }

        messageSaverManager.startMessageSaverService("MainActivity")
        lifecycleScope.launch(Dispatchers.IO) {
            preKeyManager.checkAndProvideOPK()
        }
    }

}