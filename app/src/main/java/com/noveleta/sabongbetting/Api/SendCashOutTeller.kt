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
                val url = URL("http://$ip/main/print/printCashOutTellerAndroid.php")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                }

                val jsonBody = JSONObject().apply {
                    put("userID", userID)
                    put("roleID", roleID)
                    put("cashOutTellerPassword", cashHandlerPassword)
                    put("generate_cashoutteller", true)
                    put("cashOutTellerID", cashHandlerId)
                    put("cname", SessionManager.cname ?: "N/A")
                    put("cashOutAmount", cashOutAmmount)
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
                val resultInt = json.optInt("resultCode", 0)
                val message = json.getString("message")
                
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

