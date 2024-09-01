package com.szlazakm.safechat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.szlazakm.safechat.client.data.services.MessageSaverManager
import com.szlazakm.safechat.client.presentation.StarterRoutes
import com.szlazakm.safechat.client.presentation.mainGraph
import com.szlazakm.safechat.client.presentation.starterGraph
import com.szlazakm.safechat.client.presentation.userCreationGraph
//import com.szlazakm.safechat.client.presentation.userCreationGraph
import com.szlazakm.safechat.ui.theme.SafeChatTheme
import com.szlazakm.safechat.utils.auth.PreKeyManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preKeyManager: PreKeyManager

    @Inject
    lateinit var messageSaverManager: MessageSaverManager

    override fun onCreate(savedInstanceState: Bundle?) {

        setupBouncyCastle()

        Log.d("MainActivity", "Main activity started")
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            SafeChatTheme {
                NavHost(
                    navController = navController,
                    startDestination = StarterRoutes.Start.route
                ) {
                    starterGraph(navController = navController)
                    userCreationGraph(navController = navController)
                    mainGraph(navController = navController)
                }
            }
        }

        messageSaverManager.startMessageSaverService("MainActivity")
        lifecycleScope.launch(Dispatchers.IO) {
            preKeyManager.checkAndProvideOPK()
        }
    }

    private fun setupBouncyCastle() {
//        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
//            ?: // Web3j will set up the provider lazily when it's first used.
//            return

        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }
}