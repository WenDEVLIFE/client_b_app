package com.noveleta.sabongbetting.Api

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

import android.content.Context
import android.widget.Toast

import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*

class SendCashInTellerViewModel : ViewModel() {

    private val _betResponse = MutableStateFlow<CashinResponse?>(null)
    val betResponse: StateFlow<CashinResponse?> = _betResponse

    private val _betResult = MutableStateFlow<Int?>(null)
    val betResult: StateFlow<Int?> = _betResult
    
    private val _betErrorCode = MutableStateFlow<Int?>(null)
    val betErrorCode: StateFlow<Int?> = _betErrorCode

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendCashInTeller(
    context: Context,
        userID: String,
        roleID: String,
        cashOutAmmount: String,
        cashHandlerId: Int,
        cashHandlerPassword: String,
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val ip = SessionManager.ipAddress.orEmpty().takeIf { it.isNotBlank() } ?: "192.168.8.100"
                val url = URL("http://$ip/main/print/printCashInTellerAndroid.php")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                }

                val jsonBody = JSONObject().apply {
                    put("userID", userID)
                    put("roleID", roleID)
                    put("cashInHandlerPassword", cashHandlerPassword)
                    put("generateCashInTeller", true)
                    put("cashInHandlerId", cashHandlerId)
                    put("cname", SessionManager.cname ?: "N/A")
                    put("cashInAmmount", cashOutAmmount)
                }

                withContext(Dispatchers.IO) {
                    conn.outputStream.bufferedWriter().use { it.write(jsonBody.toString()) }
                }

                val responseText = withContext(Dispatchers.IO) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                }

                val json = JSONObject(responseText)
                val success = json.getBoolean("success")
                val resultInt = json.optInt("resultCode", 0)
                val message = json.getString("message")
                
                if(success){
                _betResult.value = 0
                }

                if (success) {
                 Log.e("WebSocket", "Response: Success Cash In, message = $message")
                    _betResponse.value = CashinResponse(
    success = json.getBoolean("success"),
    message = json.getString("message"),
    resultCode = json.getInt("resultCode"),
    cashinAmount = json.getString("cashin_amount"),
    cashinHandlerUsername = json.getString("cashin_handler_username"),
    transactionDate = json.getString("transaction_date"),
    cashierUsername = json.getString("cashier_username"),
    systemName = json.getString("system_name"),
    barcode = json.getString("barcode"),
    eventId = json.getInt("event_id"),
    cashHandlerId = json.getInt("cash_handler_id"),
    roleId = json.getInt("role_id")
)

                } else {
                    _betResponse.value = null
                    _betErrorCode.value = -1
                    _betResult.value = resultInt
                    Log.e("WebSocket", "Response: $resultInt, $message")
                    Toast.makeText(context, "Response: $resultInt, $message", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _betResult.value = null
                _betErrorCode.value = 0
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
        _betErrorCode.value = 0
        _betResponse.value = null
    }
}

