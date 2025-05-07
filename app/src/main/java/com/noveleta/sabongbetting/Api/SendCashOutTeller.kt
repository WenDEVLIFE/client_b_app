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

import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*

class SendCashOutTellerViewModel : ViewModel() {

    private val _betResponse = MutableStateFlow<CashoutResponse?>(null)
    val betResponse: StateFlow<CashoutResponse?> = _betResponse

    private val _betResult = MutableStateFlow<Int?>(null)
    val betResult: StateFlow<Int?> = _betResult
    
    private val _betErrorCode = MutableStateFlow<Int?>(null)
    val betErrorCode: StateFlow<Int?> = _betErrorCode
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendCashOutTeller(
        userID: String,
        roleID: String,
        cashOutAmmount: Int,
        cashHandlerId: Int,
        cashHandlerPassword: String,
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val ip = SessionManager.ipAddress.orEmpty().takeIf { it.isNotBlank() } ?: "192.168.8.100"
                val url = URL("http://$ip/main/print/printCashOutTellerAndroid.php")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                }

                val jsonBody = JSONObject().apply {
                    put("userID", userID)
                    put("roleID", roleID)
                    put("cashHandlerPassword", cashHandlerPassword)
                    put("generate_cashoutteller", true)
                    put("cashHandlerId", cashHandlerId)
                    put("cashOutAmmount", cashOutAmmount)
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
                }

                if (success) {
                    _betResponse.value = CashoutResponse(
    resultCode = json.getInt("resultCode"),
    message = json.getString("message"),
    transactionDate = json.getString("transactionDate"),
    systemName = json.getString("systemName"),
    cashier = json.getString("cashier"),
    transactionType = json.getString("transactionType"),
    cashoutAmount = json.getString("cashoutAmount"),
    cashHandler = json.getString("cashHandler")
)

                } else {
                    _betResponse.value = null
                    _betErrorCode.value = -1
                    _betResult.value = resultInt
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _betResult.value = null
                _betErrorCode.value = 0
                _betResponse.value = null
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

