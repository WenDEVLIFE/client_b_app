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
   
    private val _dashboardData = MutableLiveData<LiveBettingData?>(null)
    val dashboardData: LiveData<LiveBettingData?> = _dashboardData
    
    private lateinit var webSocket: WebSocket
    private var retryCount = 0
    private val maxRetries = 5
    
    private var updateJob: Job? = null

private fun startPeriodicSubscription() {
    updateJob = viewModelScope.launch {
        while (isActive) {
            delay(5000L) // every 5 seconds
            val subscribe = """{"type":"androidDashboard","roleID":${SessionManager.roleID?.toIntOrNull() ?: 2}, "companyID": "${SessionManager.accountID}"}"""
            webSocket.send(subscribe)
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
          val ip = SessionManager.ipAddress?.takeIf { it.isNotBlank() } ?: "192.168.8.100"
          val port = SessionManager.portAddress?.takeIf { it.isNotBlank() } ?: "8080"
    
          val sessionId = SessionManager.sessionId
          val userRole = SessionManager.roleID?.toIntOrNull() ?: 2
          val request = Request.Builder()
              .url("ws://$ip:$port")
              .apply { sessionId?.let { addHeader("Cookie", "PHPSESSID=$it") } }
              .build()
          val client = OkHttpClient()
        
          val listener = object : WebSocketListener() {
        
          override fun onOpen(ws: WebSocket, resp: okhttp3.Response) {
              retryCount = 0
              Log.d("WebSocket", "Connection opened with response code: ${resp.code}")
              Log.d("WebSocket", "Headers: ${resp.headers}")
         
              startPeriodicSubscription()
              
           }
    
            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("WebSocket", "Raw message: $text")
                try {
                    val obj = JSONObject(text)
                    val success = obj.optString("success")
                    Log.d("WebSocket", "Parsed success field: $success")
  
                    when (success) {
        
                        "androidDashboard" -> {
                            Log.d("WebSocket", "Parsing androidDashboard")
                            val dash = Gson().fromJson(text, LiveBettingData::class.java)
                            Log.d("WebSocket", "Parsed: $dash")
                            
                            // Update value on main thread with LiveData
                            _dashboardData.postValue(dash)
                            Log.d("WebSocket", "Posted value to LiveData")
                        }
                        
                        else -> {
                            Log.w("WebSocket", "Unhandled: ${obj.optString("success")}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "JSON error: ${e.message}")
                }
            }
            override fun onFailure(ws: WebSocket, t: Throwable, resp: okhttp3.Response?) {
               Log.e("WebSocket", "Connection failure", t)
               Log.e("WebSocket", "Response: ${resp?.message}")
               retryCount++
            }
            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
            stopPeriodicSubscription()
               Log.d("WebSocket", "Connection closing: code=$code, reason=$reason")
            }
            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
            stopPeriodicSubscription()
               Log.d("WebSocket", "Connection closed: code=$code, reason=$reason")
             }
        }
        webSocket = client.newWebSocket(request, listener)
    }
    
    override fun onCleared() {
        super.onCleared()
        stopPeriodicSubscription()
        if (::webSocket.isInitialized) webSocket.cancel()
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