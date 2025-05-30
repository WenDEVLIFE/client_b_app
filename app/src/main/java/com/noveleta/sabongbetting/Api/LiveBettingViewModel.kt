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

    private val _liveBettingData = MutableLiveData<LiveBettingData>()
    val liveBettingData: LiveData<LiveBettingData> = _liveBettingData

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var retryCount = 0
    private val maxRetries = 5
    private var webSocket: WebSocket? = null

    fun connectWebSocket() {
        // Prevent reconnect if already open
       /* if (webSocket != null) {
            Log.d("LiveWebSocket", "WebSocket already initialized")
            return 
        }*/

        if (retryCount > maxRetries) {
            _errorMessage.value = "Max retries exceeded. Please check your connection."
            return
        }

        val ip = SessionManager.ipAddress?.takeIf { it.isNotBlank() } ?: "192.168.8.100"
        val port = SessionManager.portAddress?.takeIf { it.isNotBlank() } ?: "8080"
        val sessionId = SessionManager.sessionId
        val companyID = SessionManager.accountID

        val requestBuilder = Request.Builder().url("ws://$ip:$port")
        sessionId?.let { requestBuilder.addHeader("Cookie", "PHPSESSID=$it") }

        val client = OkHttpClient()
        val request = requestBuilder.build()

        val listener = object : WebSocketListener() {

            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d("LiveWebSocket", "Connected: ${response.message}")
                retryCount = 0
                _errorMessage.value = null
                webSocket = ws

                val subscribeMessage = """{"type": "androidDashboard", "roleID": 2, "companyID": "$companyID"}"""
                ws.send(subscribeMessage)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("LiveWebSocket", "Message received: $text")
                try {
                    val json = JSONObject(text)
                    val success = json.optString("success")

                    if (success == "androidDashboard") {
                        val dashboard = Gson().fromJson(text, LiveBettingData::class.java)
                        viewModelScope.launch(Dispatchers.Main) {
                            _liveBettingData.value = dashboard
                        }
                    } else {
                        Log.w("LiveWebSocket", "Unhandled message type: $success")
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Parsing error: ${e.message}"
                }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e("LiveWebSocket", "WebSocket failed: ${t.message}")
                _errorMessage.value = "WebSocket failed: ${t.message}"
                webSocket = null
                retryCount++
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                Log.d("LiveWebSocket", "Closing: $code / $reason")
                ws.close(1000, null)
                _errorMessage.value = "WebSocket Suddenly Closed: $code / $reason"
                webSocket = null
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                Log.d("LiveWebSocket", "Closed: $code / $reason")
                _errorMessage.value = "WebSocket Forced Closed: $code / $reason"
                webSocket = null
            }
        }

        client.newWebSocket(request, listener)
    }

    private fun reconnectWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            connectWebSocket()
        }, 3000)
    }

    fun closeWebSocket() {
        try {
            webSocket?.close(1000, "Manual close")
            Log.d("LiveWebSocket", "Closed manually")
        } catch (e: Exception) {
            _errorMessage.value = "Close error: ${e.message}"
        } finally {
            webSocket = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        closeWebSocket()
        Log.d("LiveWebSocket", "ViewModel destroyed")
    }
}
