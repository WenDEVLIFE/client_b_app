package com.noveleta.sabongbetting.Api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import android.util.Log
import android.os.Handler
import android.os.Looper
import org.json.JSONObject

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job

import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay

class LiveBettingViewModel : ViewModel() {

    private val _liveBettingData = MutableStateFlow<LiveBettingData?>(null)
    val liveBettingData: StateFlow<LiveBettingData?> = _liveBettingData

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private lateinit var webSocket: WebSocket
    private var isWebSocketConnected = false
    private var retryCount = 0
    private val maxRetries = 5

    fun connectWebSocket() {
        if (isWebSocketConnected) {
            Log.d("LiveWebSocket", "Already connected")
            return
        }

        if (retryCount > maxRetries) {
            val msg = "Max retries exceeded. Please check your connection."
            Log.e("LiveWebSocket", msg)
            _errorMessage.value = msg
            return
        }

        val sessionId = SessionManager.sessionId
        val ip = SessionManager.ipAddress?.takeIf { it.isNotBlank() } ?: "192.168.8.100"
        val port = SessionManager.portAddress?.takeIf { it.isNotBlank() } ?: "8080"

        val requestBuilder = Request.Builder().url("ws://$ip:$port")
        sessionId?.let {
            requestBuilder.addHeader("Cookie", "PHPSESSID=$it")
        }

        val request = requestBuilder.build()
        val client = OkHttpClient()

        val listener = object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("LiveWebSocket", "Connected: ${response.message}")
                isWebSocketConnected = true
                retryCount = 0
                _errorMessage.value = null // Clear previous error

                val subscribeMessage = """{"type": "androidDashboard", "roleID": 2}"""
                webSocket.send(subscribeMessage)
                Log.d("LiveWebSocket", "Sent subscription message: $subscribeMessage")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("LiveWebSocket", "Message received: $text")
                try {
                    val jsonObject = JSONObject(text)
                    val success = jsonObject.optString("success")

                    if (success == "androidDashboard") {
                        val dashboard = Gson().fromJson(text, LiveBettingData::class.java)
                        viewModelScope.launch(Dispatchers.Main) {
                            _liveBettingData.value = dashboard
                        }
                    } else {
                        Log.w("LiveWebSocket", "Unhandled type: $success")
                    }

                } catch (e: Exception) {
                    val error = "Parsing error: ${e.message}"
                    Log.e("LiveWebSocket", error)
                    _errorMessage.value = error
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                val error = "WebSocket failed: ${t.message}"
                Log.e("LiveWebSocket", error)
                isWebSocketConnected = false
                _errorMessage.value = error
                retryCount++
                Handler(Looper.getMainLooper()).postDelayed({
                    connectWebSocket()
                }, 3000)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("LiveWebSocket", "Closing: $code / $reason")
                isWebSocketConnected = false
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("LiveWebSocket", "Closed: $code / $reason")
                isWebSocketConnected = false
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    fun closeWebSocket() {
        try {
            if (::webSocket.isInitialized) {
                webSocket.close(1000, "Manual close")
                Log.d("LiveWebSocket", "Closed manually")
            }
        } catch (e: Exception) {
            val error = "Close error: ${e.message}"
            Log.e("LiveWebSocket", error)
            _errorMessage.value = error
        } finally {
            isWebSocketConnected = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        closeWebSocket()
        Log.d("LiveWebSocket", "ViewModel destroyed")
    }
}
