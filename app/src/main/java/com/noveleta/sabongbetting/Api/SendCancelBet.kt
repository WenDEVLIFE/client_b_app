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

import kotlinx.coroutines.flow.asStateFlow

import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*

class SendCancelBetViewModel : ViewModel() {

    private val _betResponse = MutableStateFlow<CancelledBetResponse?>(null)
    val betResponse: StateFlow<CancelledBetResponse?> = _betResponse
    
    private val _transactionCode = MutableStateFlow("")
    val transactionCode: StateFlow<String> = _transactionCode.asStateFlow()

    fun setTransactionCode(code: String) {
        _transactionCode.value = code
    }
    
    private val _betResult = MutableStateFlow<Int?>(null)
    val betResult: StateFlow<Int?> = _betResult
    
    private val _betErrorCode = MutableStateFlow<Int?>(null)
    val betErrorCode: StateFlow<Int?> = _betErrorCode
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendCancelBetBarcode(
        userID: String,
        roleID: String,
        barcodeTxt: Int,
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val ip = SessionManager.ipAddress.orEmpty().takeIf { it.isNotBlank() } ?: "192.168.8.100"
                val url = URL("http://$ip/main/print/printCancelTicketBetAndroid.php")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                }

                val jsonBody = JSONObject().apply {
                    put("userID", userID)
                    put("roleID", roleID)
                    put("txtBarCode", barcodeTxt)
                }

                withContext(Dispatchers.IO) {
                    conn.outputStream.bufferedWriter().use { it.write(jsonBody.toString()) }
                }

                val responseText = withContext(Dispatchers.IO) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                }

                val json = JSONObject(responseText)
                val success = json.getBoolean("success")
                val resultInt = json.getInt("resultCode")
                
                if(success){
                _betResult.value = 0
                _betResult.value = resultInt
                _betErrorCode.value = 0
                }

                if (success) {
                    _betResponse.value = CancelledBetResponse(
    barcode = json.getString("barcode"),
    dateTime = json.getString("dateTime"),
    systemName = json.getString("systemName"),
    cashier = json.getString("cashier"),
    fightNumber = json.getString("fightNumber"),
    side = json.getString("side"),
    amount = json.getString("amount"),
    logAction = json.getString("logAction")
)
                } else {
                    _betResponse.value = null
                    _betResult.value = resultInt
                    _betErrorCode.value = -1
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _betResult.value = null
                _betResponse.value = null
                _betErrorCode.value = null
            }

            _isLoading.value = false
        }
    }

    fun clearBetState() {
        _betResult.value = null
        _betResponse.value = null
        _betErrorCode.value = null
    }
}


