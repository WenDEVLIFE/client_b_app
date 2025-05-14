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
            // build URL/connection
            val ip = SessionManager.ipAddress
                .orEmpty()
                .takeIf { it.isNotBlank() }
                ?: "192.168.8.100"
            val url = URL("http://$ip/main/print/printBetPayoutAndroid.php")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 5000
                readTimeout = 5000
            }

            // write JSON body
            val jsonBody = JSONObject().apply {
                put("userID", userID)
                put("roleID", roleID)
                put("cname", SessionManager.cname ?: "N/A")
                put("txtBarCode", barcodeResult)
            }
            withContext(Dispatchers.IO) {
                conn.outputStream.bufferedWriter().use { it.write(jsonBody.toString()) }
            }

            // get status & choose the right stream
            val status = conn.responseCode
            val rawResponse = withContext(Dispatchers.IO) {
                val stream = if (status == HttpURLConnection.HTTP_OK)
                    conn.inputStream
                else
                    conn.errorStream
                stream.bufferedReader().use { it.readText() }
            }

            if (status != HttpURLConnection.HTTP_OK) {
                // For debugging: show the raw HTML or PHP error
                throw IOException("Server returned HTTP $status: $rawResponse")
            }

            // parse the JSON we know is valid
            val json = JSONObject(rawResponse)
            val success   = json.getBoolean("success")
            val message   = json.getString("message")
            val resultInt = json.optInt("errorCode", 0)

            if (success) {
                _betResult.value    = 0
                _betErrorCode.value = 0
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

                Toast
                    .makeText(context,
                              "Successfully claimed payout: $message",
                              Toast.LENGTH_LONG)
                    .show()
            } else {
                _betResponse.value  = null
                _betResult.value    = resultInt
                _betErrorCode.value = -1

                Toast
                    .makeText(context,
                              "Error $resultInt: $message",
                              Toast.LENGTH_LONG)
                    .show()
            }
        }
        catch (e: Exception) {
            // Now you'll see either your IOException with the HTML body,
            // or the real parsing error if something else blew up.
            Log.e("claimPayout", "failed", e)
            _betResult.value    = -1
            _betErrorCode.value = -1
            _betResponse.value  = null

            Toast
                .makeText(context,
                          "Network/parse error: ${e.localizedMessage}",
                          Toast.LENGTH_LONG)
                .show()
        }
        finally {
            _isLoading.value = false
        }
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


