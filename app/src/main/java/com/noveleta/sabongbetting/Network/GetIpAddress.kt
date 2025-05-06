package com.noveleta.sabongbetting.Network

import android.content.Context
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun getWifiIpAddress(context: Context): String {
    return try {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val wifiInfo = wifiManager?.connectionInfo

        if (wifiManager != null && wifiManager.isWifiEnabled && wifiInfo != null && wifiInfo.ipAddress != 0) {
            val ipInt = wifiInfo.ipAddress
            InetAddress.getByAddress(
                ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()
            ).hostAddress ?: "192.168.8.1xx"
        } else {
            // Not connected to WiFi or IP is invalid
            "192.168.8.1xx" 
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "192.168.8.1xx" 
    }
}

