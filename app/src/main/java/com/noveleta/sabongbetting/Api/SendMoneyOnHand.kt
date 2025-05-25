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

import org.json.JSONException
import kotlinx.coroutines.flow.asStateFlow

import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*

class SendMoneyOnHandViewModel : ViewModel() {

    private val _betResponse = MutableStateFlow<SummaryReport?>(null)
    val betResponse: StateFlow<SummaryReport?> = _betResponse

    private val _betResult = MutableStateFlow<Int?>(null)
    val betResult: StateFlow<Int?> = _betResult
    
    private val _betErrorCode = MutableStateFlow<Int?>(null)
    val betErrorCode: StateFlow<Int?> = _betErrorCode

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMoneyOnHandReport(
        context: Context,
        userID: String,
        roleID: String,
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val ip = SessionManager.ipAddress.orEmpty().takeIf { it.isNotBlank() } ?: "192.168.8.100"
                val url = URL("http://$ip/main/Android/printMoneyOnHandAndroid.php")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                }

                val jsonBody = JSONObject().apply {
                    put("companyID", userID)
                    put("roleID", roleID)
                    put("cname", SessionManager.cname ?: "N/A")
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
                val resultInt = json.optInt("errorCode", 0)
                val message = json.getString("message")
                
                if(success){
                _betResult.value = 0
                }

                if (success) {
                 Log.e("WebSocket", "Response: Success Cash In, message = $message")
                    _betResponse.value = SummaryReport(
    errorCode = json.getInt("errorCode"),
    reportTitle = json.getString("reportTitle"),
    systemName = json.getString("systemName"),
    dateTime = json.getString("dateTime"),
    username = json.getString("username"),
    cashIn = json.getString("cashIn"),
    totalBets = json.getString("totalBets"),
    totalMobileDeposit = json.getString("totalMobileDeposit"),
    cashOut = json.getString("cashOut"),
    totalPayoutPaid = json.getString("totalPayoutPaid"),
    totalCancelledPaid = json.getString("totalCancelledPaid"),
    totalCancelledBet = json.getString("totalCancelledBet"),
    totalDrawPaid = json.getString("totalDrawPaid"),
    totalDrawBets = json.getString("totalDrawBets"),
    totalDrawBetsPaid = json.getString("totalDrawBetsPaid"),
    totalMobileWithdraw = json.getString("totalMobileWithdraw"),
    moneyOnHand = json.getString("moneyOnHand"),
    commisionBody = json.getString("commisionBody"),
    totalPayoutUnclaimed = json.getString("totalPayoutUnclaimed"),
    totalDrawUnclaimed = json.getString("totalDrawUnclaimed")
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


