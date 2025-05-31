package com.noveleta.sabongbetting.Network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(private val context: Context) {

    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val telephonyManager =
        context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isConnected.value = true
        }

        override fun onLost(network: Network) {
            _isConnected.value = false
        }
    }

    private var isRegistered = false

    fun register() {
        if (isRegistered) return

        // Set initial connectivity status
        val active = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(active)
        _isConnected.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, networkCallback)
        isRegistered = true
    }

    fun unregister() {
        if (!isRegistered) return
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isRegistered = false
    }

    /**
     * Returns signal level:
     * - 0 (Poor)
     * - 1 (Low)
     * - 2 (Good)
     * - 3 (Great)
     * - Returns -1 if unknown
     */
    fun getSignalLevel(): Int {
        val activeNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork)

        return when {
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> getWifiSignalLevel()
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> getMobileSignalLevel()
            else -> -1 // Unknown or no signal
        }
    }

    private fun getWifiSignalLevel(): Int {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        return WifiManager.calculateSignalLevel(info.rssi, 4) // 0 to 3
    }

    private fun getMobileSignalLevel(): Int {
        val signalStrength = telephonyManager.signalStrength
        return signalStrength?.level ?: -1 // level = 0 (worst) to 4 (best)
    }
}
