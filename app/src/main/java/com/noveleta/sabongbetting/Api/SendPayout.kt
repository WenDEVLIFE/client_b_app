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
                put("systemName", SessionManager.systemName ?: "N/A")
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
        transactionCode        = if (json.getString("transactionCode").isNotBlank()) json.getString("transactionCode") else "N/A",
        transactionFightNumber = json.optInt("transactionFightNumber", 0),
        transactionSide        = if (json.getString("transactionSide").isNotBlank()) json.getString("transactionSide") else "N/A",
        transactionAmount      = if (json.getString("transactionAmount").isNotBlank()) json.getString("transactionAmount") else "0",
        transactionType        = if (json.getString("transactionType").isNotBlank()) json.getString("transactionType") else "N/A",
        transactionDate        = if (json.getString("transactionDate").isNotBlank()) json.getString("transactionDate") else "N/A",
        transactionCashier     = if (json.getString("transactionCashier").isNotBlank()) json.getString("transactionCashier") else "N/A",
        transactionSystemName  = if (json.getString("transactionSystemName").isNotBlank()) json.getString("transactionSystemName") else "N/A",
        transactionOddsMeron   = if (json.getString("transactionOddsMeron").isNotBlank()) json.getString("transactionOddsMeron") else "0",
        transactionOddsWala    = if (json.getString("transactionOddsWala").isNotBlank()) json.getString("transactionOddsWala") else "0",
        transactionPayout      = if (json.getString("transactionPayout").isNotBlank()) json.getString("transactionPayout") else "0",
        betType                = json.optInt("betType", 0),
        type                   = if (json.getString("type").isNotBlank()) json.getString("type") else "N/A",
        roleID                 = if (json.getString("roleID").isNotBlank()) json.getString("roleID") else "N/A",
        userID                 = if (json.getString("userID").isNotBlank()) json.getString("userID") else "N/A",
        ticketLogo             = if (json.getString("ticketLogo").isNotBlank()) json.getString("ticketLogo") else "N/A",
        barcode                = if (json.getString("barcode").isNotBlank()) json.getString("barcode") else "N/A",
        dateTime               = if (json.getString("dateTime").isNotBlank()) json.getString("dateTime") else "N/A",
        systemName             = if (json.getString("systemName").isNotBlank()) json.getString("systemName") else "N/A",
        cashier                = if (json.getString("cashier").isNotBlank()) json.getString("cashier") else "N/A",
        status                 = if (json.getString("status").isNotBlank()) json.getString("status") else "N/A",
        fightNumber            = json.optInt("fightNumber", 0),
        side                   = if (json.getString("side").isNotBlank()) json.getString("side") else "N/A",
        amount                 = if (json.getString("amount").isNotBlank()) json.getString("amount") else "0",
        odds                   = if (json.getString("odds").isNotBlank()) json.getString("odds") else "0",
        payout                 = if (json.getString("payout").isNotBlank()) json.getString("payout") else "0"
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
            _betResult.value = -1
            _betErrorCode.value = -1
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
}


