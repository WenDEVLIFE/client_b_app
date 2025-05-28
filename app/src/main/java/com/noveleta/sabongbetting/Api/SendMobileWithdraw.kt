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

class SendMobileWithdrawViewModel : ViewModel() {

    private val _betResponse = MutableStateFlow<MobileWithdrawResponse?>(null)
    val betResponse: StateFlow<MobileWithdrawResponse?> = _betResponse

    private val _betResult = MutableStateFlow<Int?>(null)
    val betResult: StateFlow<Int?> = _betResult
    
    private val _betErrorCode = MutableStateFlow<Int?>(null)
    val betErrorCode: StateFlow<Int?> = _betErrorCode
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _transactionCode = MutableStateFlow("")
    val transactionCodeWithdraw: StateFlow<String> = _transactionCode.asStateFlow()
    
    fun setTransactionCode(code: String) {
        _transactionCode.value = code
    }
    
    fun sendMobileWithdraw(
    context: Context,
    userID: String,
    roleID: String,
    barcodeResult: String
) {
    viewModelScope.launch {
        _isLoading.value = true

        try {
            val ip = SessionManager.ipAddress.orEmpty().takeIf { it.isNotBlank() } ?: "192.168.8.100"
            val url = URL("http://$ip/main/Android/printMobileWithdrawAndroid.php")
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
    Log.e("WebSocket", "Response: CLAIMED WITHDRAW SUCCESS!, message = $message")

val withdrawResponse = MobileWithdrawResponse(
    success = json.getBoolean("success"),
    message = json.getString("message"),
    barcode = json.getString("barcode"),
    accountID = json.getString("accountID"),
    mobileNumber = json.getString("mobileNumber"),
    withdrawnAmount = json.getString("withdrawnAmount"),
    previousBalance = json.getString("previousBalance"),
    newBalance = json.getString("newBalance"),
    timestamp = json.getString("timestamp"),
    cashier = json.getString("cashier"),
    systemName = json.getString("systemName"),
    logAction = json.getString("logAction"),
    currentBalance = json.getString("currentBalance")
)

_betResponse.value = withdrawResponse
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



