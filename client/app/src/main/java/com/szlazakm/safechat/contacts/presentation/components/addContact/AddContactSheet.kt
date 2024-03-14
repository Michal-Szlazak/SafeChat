package com.szlazakm.safechat.contacts.presentation.components.addContact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AddContactScreen(
    navController: NavController,
    viewModel: AddContactViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = 4.dp
            )
        }
    ) {
        padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Add text field for search
            TextField(
                value = "",
                onValueChange = { /* Update search query state */ },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}