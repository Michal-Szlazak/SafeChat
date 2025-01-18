package com.szlazakm.safechat.client.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.szlazakm.safechat.client.data.entities.UserEntity
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.utils.auth.MessageDecryptor
import com.szlazakm.safechat.utils.auth.PreKeyManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class OPKSupplierService: Service() {

    @Inject
    lateinit var preKeyManager: PreKeyManager
    @Inject
    lateinit var userRepository: UserRepository

    private val stompService: StompService = StompService()
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private lateinit var networkMonitor: NetworkMonitor

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SafeChat:OPKSupplierService", "onStart called")
        serviceScope.launch{
            connectToQueue()
        }

        scheduleRefillOpk()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        networkMonitor = NetworkMonitor(this)

        networkMonitor.registerNetworkCallback(
            onNetworkAvailable = {
                Log.d("OPKSupplierService", "Network available.")
                reconnectStompService()
            },
            onNetworkLost = {
                stompService.disconnect()
                Log.d("OPKSupplierService", "Network lost. Stomp service will be disconnected.")
            }
        )

        Log.d("SafeChat:OPKSupplierService", "onCreate called")
    }

    private fun reconnectStompService() {
        Log.d("OPKSupplierService", "Network available. Reconnecting STOMP service...")
        serviceScope.launch {
            connectToQueue()
        }
    }

    override fun onDestroy() {

        super.onDestroy()
        stompService.disconnect()
        Log.d("SafeChat:OPKSupplierService", "onDestroy called")
    }

    private fun connectToQueue() {

        val localUser: UserEntity

        try {
            localUser = userRepository.getLocalUser()
        } catch (e: Exception) {
            Log.e(
                "SafeChat:OPKSupplierService",
                "Exception while trying to get local user. Aborting OPKSupplierService creation. E: $e"
            )
            return
        }

        stompService.subscribeToTopic("/user/notification/${localUser.phoneNumber}") { message ->

            Log.d("SafeChat:OPKSupplierService","received a message $message")

            serviceScope.launch {
                preKeyManager.checkAndProvideOPK()
            }
        }
    }

    private fun scheduleRefillOpk() {

        val timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                Log.d("SafeChat:OPKSupplierService", "Refilling OPK")
                serviceScope.launch {
                    preKeyManager.checkAndProvideOPK()
                }
            }
        }, 0, 1000 * 60 * 60 * 24)

    }
}