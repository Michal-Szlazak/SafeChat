package com.szlazakm.safechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.szlazakm.safechat.contacts.presentation.SafeChatApp
import com.szlazakm.safechat.ui.theme.SafeChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SafeChatTheme {
                SafeChatApp()
            }
        }
    }

}