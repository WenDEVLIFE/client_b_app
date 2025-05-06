package com.noveleta.sabongbetting.Network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(context: Context) {
    // 1) Grab the ConnectivityManager via the string‐based API
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // 2) Backing Flow to emit connectivity state
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // 3) Our NetworkCallback to update the Flow
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isConnected.value = true
        }
        override fun onLost(network: Network) {
            _isConnected.value = false
        }
    }

    init {
        // 4) Prime the initial value from whatever's currently active
        val active = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(active)
        _isConnected.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        // 5) Register for future updates (any Internet‐capable transport)
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
