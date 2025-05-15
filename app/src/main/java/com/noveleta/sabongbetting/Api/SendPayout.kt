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
import java.io.IOException

import android.content.Context
import android.widget.Toast


import kotlinx.coroutines.flow.asStateFlow


import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*

class SendPayoutViewModel : ViewModel() {

    private val _betResponse = MutableStateFlow<BetPayoutResponse?>(null)
    val betResponse: StateFlow<BetPayoutResponse?> = _betResponse
    
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

fun claimPayout(
    context: Context,
    userID: String,
    roleID: String,
    barcodeResult: String
) {
    viewModelScope.launch {
        _isLoading.value = true

        try {
            val ip = SessionManager.ipAddress.orEmpty().takeIf { it.isNotBlank() } ?: "192.168.8.100"
            val url = URL("http://$ip/main/print/printBetPayoutAndroid.php")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            val jsonBody = JSONObject().apply {
                put("userID", userID)
                put("roleID", roleID)
                put("cname", SessionManager.cname ?: "N/A")
                put("txtBarCode", barcodeResult)
            }

            withContext(Dispatchers.IO) {
                conn.outputStream.bufferedWriter().use { it.write(jsonBody.toString()) }
            }

            val responseText = withContext(Dispatchers.IO) {
                conn.inputStream.bufferedReader().use { it.readText() }
            }

            Log.d("CancelBetResponse", "Response: $responseText")

            val json = JSONObject(responseText)
            val success = json.getBoolean("success")
            val message = json.getString("message")
            val resultInt = json.optInt("errorCode", 0)

            if (success) {
                val response = BetPayoutResponse(
                    success                 = true,
                    transactionCode         = json.optString("transactionCode", ""),
                    transactionFightNumber  = json.optInt("transactionFightNumber", 0),
                    transactionSide         = json.optString("transactionSide", ""),
                    transactionAmount       = json.optString("transactionAmount", "0"),
                    transactionType         = json.optString("transactionType", ""),
                    transactionDate         = json.optString("transactionDate", ""),
                    transactionCashier      = json.optString("transactionCashier", ""),
                    transactionSystemName   = json.optString("transactionSystemName", ""),
                    transactionOddsMeron    = json.optString("transactionOddsMeron", "0"),
                    transactionOddsWala     = json.optString("transactionOddsWala", "0"),
                    transactionPayout       = json.optString("transactionPayout", "0"),
                    betType                 = json.optInt("betType", 0),
                    type                    = json.optString("type", ""),
                    roleID                  = json.optString("roleID", ""),
                    userID                  = json.optString("userID", ""),
                    ticketLogo              = json.optString("ticketLogo", ""),
                    barcode                 = json.optString("barcode", ""),
                    dateTime                = json.optString("dateTime", ""),
                    systemName              = json.optString("systemName", ""),
                    cashier                 = json.optString("cashier", ""),
                    status                  = json.optString("status", ""),
                    fightNumber             = json.optInt("fightNumber", 0),
                    side                    = json.optString("side", ""),
                    amount                  = json.optString("amount", "0"),
                    odds                    = json.optString("odds", "0"),
                    payout                  = json.optString("payout", "0")
                )

                _betResponse.value   = response
                _betResult.value     = resultInt
                _betErrorCode.value  = 0

                Toast.makeText(
                    context,
                    "Successfully claimed payout: $message",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                _betResponse.value   = null
                _betResult.value     = resultInt
                _betErrorCode.value  = -1

                Toast.makeText(
                    context,
                    "Error $resultInt: $message",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            Log.e("CancelBetError", "Exception: ${e.message}", e)
            _betResult.value   = null
            _betResponse.value = null
            _betErrorCode.value= null

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
    
    fun JSONObject.safeGetString(key: String, default: String = "N/A"): String {
    return try {
        val value = this.getString(key)
        if (value.isNotBlank()) value else default
    } catch (e: Exception) {
        default
    }
}

fun JSONObject.safeGetInt(key: String, default: Int = 0): Int {
    return try {
        this.optInt(key, default)
    } catch (e: Exception) {
        default
    }
}


}


