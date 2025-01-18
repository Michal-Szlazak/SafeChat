package com.szlazakm.safechat.client.data.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

class NetworkMonitor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isConnected(): Boolean {
        val activeNetwork = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (activeNetwork != null) {
            return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }

        return false
    }

    fun registerNetworkCallback(onNetworkAvailable: () -> Unit, onNetworkLost: () -> Unit) {
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                onNetworkAvailable()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                onNetworkLost()
            }
        })
    }
}
