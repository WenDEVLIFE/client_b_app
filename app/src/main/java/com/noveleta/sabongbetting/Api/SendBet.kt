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

import android.content.Context
import android.widget.Toast


import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONException

import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*

class BettingViewModel : ViewModel() {

    private val _betResponse = MutableStateFlow<BetResponse?>(null)
    val betResponse: StateFlow<BetResponse?> = _betResponse

    private val _betResult = MutableStateFlow<Int?>(null)
    val betResult: StateFlow<Int?> = _betResult
    
    private val _betErrorCode = MutableStateFlow<Int?>(null)
    val betErrorCode: StateFlow<Int?> = _betErrorCode
    
    private val _betMessage = MutableStateFlow<String?>(null)
    val betMessage: StateFlow<String?> = _betMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun placeBet(
    context: Context,
        userID: String,
        roleID: String,
        drawTotalBet: String,
        betType: Int,
        betAmount: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val ip = SessionManager.ipAddress.orEmpty().takeIf { it.isNotBlank() } ?: "192.168.8.100"
                val url = URL("http://$ip/main/Android/printBetAndroid.php")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                }

                val jsonBody = JSONObject().apply {
                    put("userID", userID)
                    put("roleID", roleID)
                    put("drawTotalBet", drawTotalBet)
                    put("cname", SessionManager.cname ?: "N/A")
                    put("betType", betType)
                    put("betAmount", betAmount.toDouble())
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
                val resultInt = json.getInt("result")
                val resultString = json.optString("message", "")
                
                if(success){
                _betResult.value = 0
                }

                if (success) {
                val amount = json.getString("amount")
                
                    _betResponse.value = BetResponse(
                        success = true,
                        barcode = json.getString("barcode"),
                        fightNumber = json.getInt("fightNumber"),
                        betType = json.getInt("betType"),
                        amount = amount,
                        transactionDate = json.getString("transactionDate"),
                        cashier = json.getString("cashier"),
                        systemName = json.getString("systemName")
                    )
                } else {
                    _betResponse.value = null
                    _betMessage.value = resultString
                    _betErrorCode.value = -1
                    _betResult.value = resultInt
                    
                    Toast.makeText(
                    context,
                    "Error $resultInt: $resultString",
                    Toast.LENGTH_LONG
                ).show()
                
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _betResult.value = null
                _betErrorCode.value = null
                _betResponse.value = null
                
                val fullError = Log.getStackTraceString(e)
            /*Toast.makeText(
                context,
                "Payout Error:\n$fullError",
                Toast.LENGTH_LONG
            ).show()*/
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
