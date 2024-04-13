package com.szlazakm.safechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.szlazakm.safechat.contacts.presentation.SafeChatApp
import com.szlazakm.safechat.ui.theme.SafeChatTheme
import com.szlazakm.safechat.webclient.services.MessageSaverService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var messageSaverService: MessageSaverService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SafeChatTheme {
                SafeChatApp(messageSaverService = messageSaverService)
            }
        }
    }

}