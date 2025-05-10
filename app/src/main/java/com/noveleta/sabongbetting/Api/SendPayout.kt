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
                put("txtBarCode", barcodeResult.toLong())
            }

            withContext(Dispatchers.IO) {
                conn.outputStream.bufferedWriter().use { it.write(jsonBody.toString()) }
            }

            val responseText = withContext(Dispatchers.IO) {
                conn.inputStream.bufferedReader().use { it.readText() }
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
    Log.e("WebSocket", "Response: CLAIMED PAYOUT!, message = $message")
    _betResponse.value = BetPayoutResponse(
    success = true,
    transactionCode        = json.safeGetString("transactionCode"),
    transactionFightNumber = json.safeGetInt("transactionFightNumber"),
    transactionSide        = json.safeGetString("transactionSide"),
    transactionAmount      = json.safeGetString("transactionAmount"),
    transactionType        = json.safeGetString("transactionType"),
    transactionDate        = json.safeGetString("transactionDate"),
    transactionCashier     = json.safeGetString("transactionCashier"),
    transactionSystemName  = json.safeGetString("transactionSystemName"),
    transactionOddsMeron   = json.safeGetString("transactionOddsMeron", "0"),
    transactionOddsWala    = json.safeGetString("transactionOddsWala", "0"),
    transactionPayout      = json.safeGetString("transactionPayout", "0"),
    betType                = json.safeGetInt("betType", 0),
    type                   = json.safeGetString("type"),
    roleID                 = json.safeGetString("roleID"),
    userID                 = json.safeGetString("userID"),
    ticketLogo             = json.safeGetString("ticketLogo"),
    barcode                = json.safeGetString("barcode"),
    dateTime               = json.safeGetString("dateTime"),
    systemName             = json.safeGetString("systemName"),
    cashier                = json.safeGetString("cashier"),
    status                 = json.safeGetString("status"),
    fightNumber            = json.safeGetInt("fightNumber", 0),
    side                   = json.safeGetString("side"),
    amount                 = json.safeGetString("amount", "0"),
    odds                   = json.safeGetString("odds", "0"),
    payout                 = json.safeGetString("payout", "0")
)
    _betResult.value = 0
}  else {
                _betResponse.value = null
                _betResult.value = resultInt
                _betErrorCode.value = -1
                Log.e("WebSocket", "Response: $resultInt, $message")
                
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _betResult.value = null
            _betErrorCode.value = null
            _betResponse.value = null
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


