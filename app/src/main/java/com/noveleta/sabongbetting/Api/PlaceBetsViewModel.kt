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

    private var retryCount = 0
    private val maxRetries = 5
    
    private var updateJob: Job? = null

private fun startPeriodicSubscription() {
    updateJob = viewModelScope.launch {
        while (isActive) {
            delay(5000L) // every 5 seconds
             val subscribeMessages = listOf(
        """{"type": "androidViewBets", "roleID": 2}""",
        """{"type": "drawSetting", "roleID": 2}""",
        """{"type": "newFight", "roleID": 2}""",
        """{"type": "transactionHistoryAndroid", "roleID": 2, "companyID": "${SessionManager.accountID}"}"""
    )
            for (message in subscribeMessages) {
        Log.d("WebSocket", "Sending subscribe message: $message")
        webSocket.send(message)
    }
            Log.d("WebSocket", "Periodic subscription sent")
        }
    }
}

 fun stopPeriodicSubscription() {
    updateJob?.cancel()
}


    fun connectWebSocket() {
    if (retryCount > maxRetries) {
        Log.e("WebSocket", "Max retries reached, giving up.")
        return
    }
    
    val sessionId = SessionManager.sessionId
    val ip = SessionManager.ipAddress?.takeIf { it.isNotBlank() } ?: "192.168.8.100"
    val port = SessionManager.portAddress?.takeIf { it.isNotBlank() } ?: "8080"
    

    Log.d("WebSocket", "Connecting WebSocket...")
    Log.d("WebSocket", "Session ID: $sessionId")

    val requestBuilder = Request.Builder()
        .url("ws://$ip:$port")

    sessionId?.let {
        Log.d("WebSocket", "Attaching PHPSESSID: $it")
        requestBuilder.addHeader("Cookie", "PHPSESSID=$it")
    }

    val request = requestBuilder.build()
    val client = OkHttpClient()

    val listener = object : WebSocketListener() {
    
        override fun onOpen(webSocket: WebSocket, response: Response) {
    Log.d("WebSocket", "Connection opened: ${response.message}")

    startPeriodicSubscription()
    
}


        override fun onMessage(webSocket: WebSocket, text: String) {
    Log.d("WebSocket", "Message received: $text")

    try {
        val jsonObject = JSONObject(text)
        val success = jsonObject.optString("success")
        Log.d("WebSocket", "Parsed success field: $success")

        when (success) {
            "drawSetting" -> {
                Log.d("WebSocket", "Handling drawSetting event")
                _drawSettingChanged.postValue(true)
            }
            
             "androidDashboard" -> {
                Log.d("WebSocket", "Handling androidDashboard event")
                Log.d("WebSocket", "Full JSON from PHP: $text")
                
                // Attempt to parse the JSON with Gson
                val dashboard = Gson().fromJson(text, LiveBettingData::class.java)

                // Log parsed object and check individual fields
                Log.d("WebSocket", "Parsed object: $dashboard")
                Log.d("WebSocket", "Parsed fights: ${dashboard.fights}")
                Log.d("WebSocket", "Parsed fightHistory: ${dashboard.fightHistory}")

                viewModelScope.launch(Dispatchers.Main) {
                 _dashboardDataLive.value = dashboard // FIXED
                }
            }

            "newFight" -> {
                Log.d("WebSocket", "Handling newFight event")
                _newFightStarted.postValue(true)
            }

            "transactionHistoryAndroid" -> {
        Log.d("WebSocket", "Handling transactionHistoryAndroid event")
   
}

            "androidViewBets" -> {
                Log.d("WebSocket", "Handling bets event")
                val dashboard = Gson().fromJson(text, PlaceBetsData::class.java)
                viewModelScope.launch(Dispatchers.Main) {
                 _dashboardData.value = dashboard
                }
                Log.d("WebSocket", "Posted betting dashboard data: $dashboard")
            }

            else -> {
    Log.w("WebSocket", "Unhandled success type or missing: '$success'")
    Log.w("WebSocket", "Full message: $text")
}

        }

    } catch (e: Exception) {
        Log.e("WebSocket", "Failed to parse message: ${e.message}")
    }
}


        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocket", "Connection failed: ${t.message}")
            response?.let {
                Log.e("WebSocket", "Response: ${it.code} - ${it.message}")
            }
            retryCount++
            Handler(Looper.getMainLooper()).postDelayed({
                connectWebSocket()
            }, 3000) // Retry 3 seconds
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "Connection closing: Code=$code Reason=$reason")
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "Connection closed: Code=$code Reason=$reason")
        }
    }

    webSocket = client.newWebSocket(request, listener)
}


    override fun onCleared() {
        super.onCleared()
        stopPeriodicSubscription()
        Log.d("WebSocket", "ViewModel cleared - closing WebSocket")
        webSocket.cancel()
    }
    
    fun closeWebSocket() {
    try {
        webSocket.close(1000, "App backgrounded or stopped")
        Log.d("WebSocket", "WebSocket closed manually")
    } catch (e: Exception) {
        Log.e("WebSocket", "Error closing WebSocket: ${e.message}")
    }
}

}

