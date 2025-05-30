package com.noveleta.sabongbetting.Api;

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.IOException

import android.content.Context
import android.widget.Toast


import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONException

import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*

class SendMobileDepositViewModel : ViewModel() {

    private val _betResponse = MutableStateFlow<MobileDepositResponse?>(null)
    val betResponse: StateFlow<MobileDepositResponse?> = _betResponse

    private val _betResult = MutableStateFlow<Int?>(null)
    val betResult: StateFlow<Int?> = _betResult
    
    private val _betErrorCode = MutableStateFlow<Int?>(null)
    val betErrorCode: StateFlow<Int?> = _betErrorCode
    
    private val _transactionCode = MutableStateFlow("")
    val transactionDepositCode: StateFlow<String> = _transactionCode.asStateFlow()
    
    fun setTransactionCode(code: String) {
        _transactionCode.value = code
    }
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMobileDeposit(
    context: Context,
    userID: String,
    roleID: String,
    barcodeResult: String
) {
    viewModelScope.launch {
        _isLoading.value = true

        try {
            val ip = SessionManager.ipAddress.orEmpty().takeIf { it.isNotBlank() } ?: "192.168.8.100"
            val url = URL("http://$ip/main/Android/printMobileDepositAndroid.php")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            val jsonBody = JSONObject().apply {
                put("userID", userID)
                put("roleID", roleID)
                put("cname", SessionManager.cname ?: "N/A")
                put("systemName", SessionManager.systemName ?: "N/A")
                put("txtBarCode", barcodeResult)
            }

            withContext(Dispatchers.IO) {
                conn.outputStream.bufferedWriter().use { it.write(jsonBody.toString()) }
            }

            val responseText = withContext(Dispatchers.IO) {
                conn.inputStream.bufferedReader().use { it.readText() }
            }
            
            if (!responseText.trim().startsWith("{")) {
    throw JSONException("Invalid JSON: $responseText")
}

            val json = JSONObject(responseText)
            val success = json.getBoolean("success")
            val message = json.getString("message")
            val resultInt = json.optInt("errorCode", 0)

            if(success){
                _betResult.value = 0
                _betErrorCode.value = 0
                }
                
            if (success) {
    Log.e("WebSocket", "Response: Mobile deposit success!, message = $message")
    
val depositResponse = MobileDepositResponse(
    success = json.getBoolean("success"),
    result = json.getInt("result"),
    message = json.getString("message"),
    barcode = json.getString("barcode"),
    accountID = json.getString("accountID"),
    logAction = json.getString("logAction"),
    mobileNumber = json.getString("mobileNumber"),
    currentBalance = json.getString("currentBalance"),
    depositAmount = json.getString("depositAmount"),
    availablePoints = json.getString("availablePoints"),
    datetime = json.getString("datetime"),
    systemName = json.getString("systemName"),
    cashier = json.getString("cashier")
)

_betResponse.value = depositResponse

    _betResult.value = 0
}  else {
                _betResponse.value = null
                _betResult.value = resultInt
                _betErrorCode.value = -1
                Log.e("WebSocket", "Response: $resultInt, $message")
                
                Toast.makeText(
                    context,
                    "Error $resultInt: $message",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _betResult.value = null
            _betErrorCode.value = null
            _betResponse.value = null
            
            val fullError = Log.getStackTraceString(e)
            Toast.makeText(
                context,
                "Payout Error:\n$fullError",
                Toast.LENGTH_LONG
            ).show()
        }

        _isLoading.value = false
    }
}


    fun clearBetState() {
        _betResult.value = null
        _betErrorCode.value = null
        _betResponse.value = null
    }
}



