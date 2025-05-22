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

class CallWebsocketDashboard : ViewModel() {

    private val _errorResult = MutableLiveData<String?>()
    val errorResult: LiveData<String?> = _errorResult

    private lateinit var webSocket: WebSocket
    private var retryCount = 0
    private val maxRetries = 5

    data class TriggerPayload(
        val data: String = "trigger",
        val type: String,
        val roleID: Int,
        val userID: Int
    )

    fun sendDashboardTrigger() {
        connectWebSocketOnce("dashboard")
    }

    fun sendBetsTrigger() {
        connectWebSocketOnce("bets")
    }

    private fun connectWebSocketOnce(type: String) {
        val ip = SessionManager.ipAddress?.takeIf { it.isNotBlank() } ?: "192.168.8.100"
        val port = SessionManager.portAddress?.takeIf { it.isNotBlank() } ?: "8080"
        val sessionId = SessionManager.sessionId
        val roleId = SessionManager.roleID?.toIntOrNull() ?: 2
        val userId = SessionManager.accountID?.toIntOrNull() ?: 0

        val request = Request.Builder()
            .url("ws://$ip:$port")
            .apply { sessionId?.let { addHeader("Cookie", "PHPSESSID=$it") } }
            .build()

        val client = OkHttpClient()

        val listener = object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, resp: Response) {
                Log.d("WebSocket", "Connected, sending trigger: $type")

                val payload = TriggerPayload(type = type, roleID = roleId, userID = userId)
                val json = Gson().toJson(payload)

                ws.send(json)
                ws.close(1000, "Done sending")
                Log.d("WebSocket", "Sent and closed")
            }

            override fun onFailure(ws: WebSocket, t: Throwable, resp: Response?) {
                val errorMsg = t.localizedMessage ?: resp?.message ?: "WebSocket error"
                Log.e("WebSocket", "Failed: $errorMsg")
                _errorResult.postValue(errorMsg)
            }
        }

        client.newWebSocket(request, listener)
    }

    fun clearError() {
        _errorResult.postValue(null)
    }
}

