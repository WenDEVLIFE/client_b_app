package com.noveleta.sabongbetting.Api;

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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*
import org.json.JSONObject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay

import com.google.gson.reflect.TypeToken


class PlaceBetsViewModel : ViewModel() {
    private val _dashboardData = MutableLiveData<PlaceBetsData>()
    val dashboardData: LiveData<PlaceBetsData> = _dashboardData

    private val _newFightStarted = MutableLiveData<Boolean>()
    val newFightStarted: LiveData<Boolean> = _newFightStarted

    private val _drawSettingChanged = MutableLiveData<Boolean>()
    val drawSettingChanged: LiveData<Boolean> = _drawSettingChanged

    private val _drawMax = MutableLiveData<Int>()
    val drawMax: LiveData<Int> = _drawMax

    private val _transactionHistoryList = MutableLiveData<List<FightLogEntry>>()
    val transactionHistoryList: LiveData<List<FightLogEntry>> = _transactionHistoryList

    private val _dashboardDataLive = MutableStateFlow<LiveBettingData?>(null)
    val dashboardDataLive: StateFlow<LiveBettingData?> = _dashboardDataLive

    private lateinit var webSocket: WebSocket
    private var isWebSocketConnected = false
    private var retryCount = 0
    private val maxRetries = 5

    fun connectWebSocket() {
        if (isWebSocketConnected) {
            Log.d("WebSocket", "Already connected, skipping reconnect.")
            return
        }

        if (retryCount > maxRetries) {
            Log.e("WebSocket", "Max retries reached, giving up.")
            return
        }

        val sessionId = SessionManager.sessionId
        val ip = SessionManager.ipAddress?.takeIf { it.isNotBlank() } ?: "192.168.8.100"
        val port = SessionManager.portAddress?.takeIf { it.isNotBlank() } ?: "8080"

        Log.d("WebSocket", "Connecting WebSocket...")
        Log.d("WebSocket", "Session ID: $sessionId")

        val requestBuilder = Request.Builder().url("ws://$ip:$port")
        sessionId?.let {
            Log.d("WebSocket", "Attaching PHPSESSID: $it")
            requestBuilder.addHeader("Cookie", "PHPSESSID=$it")
        }

        val request = requestBuilder.build()
        val client = OkHttpClient()

        val listener = object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connection opened: ${response.message}")
                isWebSocketConnected = true
                retryCount = 0

                // Send subscription messages ONCE
                val subscribeMessages = listOf(
                    """{"type": "androidViewBets", "roleID": 2}""",
                    """{"type": "transactionHistoryAndroid", "roleID": 2, "companyID": "${SessionManager.accountID}"}"""
                )
                for (message in subscribeMessages) {
                    Log.d("WebSocket", "Sending subscribe message: $message")
                    webSocket.send(message)
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Message received: $text")
                try {
                    val jsonObject = JSONObject(text)
                    val success = jsonObject.optString("success")
                    Log.d("WebSocket", "Parsed success field: $success")

                    when (success) {
                        "drawSetting" -> _drawSettingChanged.postValue(true)

                        "androidDashboard" -> {
                            val dashboard = Gson().fromJson(text, LiveBettingData::class.java)
                            Log.d("WebSocket", "Parsed androidDashboard: $dashboard")
                            viewModelScope.launch(Dispatchers.Main) {
                                _dashboardDataLive.value = dashboard
                            }
                        }

                        "newFight" -> _newFightStarted.postValue(true)

                        "transactionHistoryAndroid" -> {
                            Log.d("WebSocket", "Handling transactionHistoryAndroid event")
                            // Optional: Parse and post to _transactionHistoryList if needed
                        }

                        "androidViewBets" -> {
                            val dashboard = Gson().fromJson(text, PlaceBetsData::class.java)
                            viewModelScope.launch(Dispatchers.Main) {
                                _dashboardData.value = dashboard
                            }
                        }

                        else -> {
                            Log.w("WebSocket", "Unhandled or missing success type: '$success'")
                            Log.w("WebSocket", "Full message: $text")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Failed to parse message: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Connection failed: ${t.message}")
                isWebSocketConnected = false
                retryCount++
                Handler(Looper.getMainLooper()).postDelayed({
                    connectWebSocket()
                }, 3000)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Connection closing: Code=$code Reason=$reason")
                isWebSocketConnected = false
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Connection closed: Code=$code Reason=$reason")
                isWebSocketConnected = false
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("WebSocket", "ViewModel cleared - closing WebSocket")
        if (::webSocket.isInitialized) webSocket.cancel()
        isWebSocketConnected = false
    }

    fun closeWebSocket() {
        try {
            if (::webSocket.isInitialized) {
                webSocket.close(1000, "App backgrounded or stopped")
                Log.d("WebSocket", "WebSocket closed manually")
            }
        } catch (e: Exception) {
            Log.e("WebSocket", "Error closing WebSocket: ${e.message}")
        } finally {
            isWebSocketConnected = false
        }
    }
}
