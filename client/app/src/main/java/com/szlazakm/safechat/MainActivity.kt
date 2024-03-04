package com.szlazakm.safechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.szlazakm.safechat.contacts.presentation.components.ContactListScreen
import com.szlazakm.safechat.contacts.presentation.components.ContactListViewModel
import com.szlazakm.safechat.ui.theme.SafeChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeChatTheme {
                val viewModel: ContactListViewModel = viewModel()
                val state by viewModel.state.collectAsState()
                ContactListScreen(state = state) {
                    
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SafeChatTheme {
        Greeting("Android")
    }
}