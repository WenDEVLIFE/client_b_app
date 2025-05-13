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
    context: Context,
        userID: String,
        roleID: String,
        barcodeTxt: String
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
                    put("txtBarCode", barcodeTxt.toInt())
                }

                withContext(Dispatchers.IO) {
                    conn.outputStream.bufferedWriter().use { it.write(jsonBody.toString()) }
                }

                val responseText = withContext(Dispatchers.IO) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                }

                Log.d("CancelBetResponse", "Response: $responseText")

                val json = JSONObject(responseText)
                val success = json.optBoolean("success", false)
                val resultInt = json.optInt("resultCode", -1)

                if (success) {
                    val response = CancelledBetResponse(
                        barcode = json.optString("barcode", ""),
                        dateTime = json.optString("dateTime", ""),
                        systemName = json.optString("systemName", ""),
                        cashier = json.optString("cashier", ""),
                        fightNumber = json.optString("fightNumber", ""),
                        side = json.optString("side", ""),
                        amount = json.optString("amount", ""),
                        logAction = json.optString("logAction", "")
                    )

                    _betResponse.value = response
                    _betResult.value = resultInt
                    _betErrorCode.value = 0

                    Toast.makeText(context, "Bet cancelled successfully", Toast.LENGTH_SHORT).show()
                } else {
                    _betResponse.value = null
                    _betResult.value = resultInt
                    _betErrorCode.value = -1

                    Toast.makeText(context, "Failed to cancel bet", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("CancelBetError", "Exception: ${e.message}", e)
                _betResult.value = null
                _betResponse.value = null
                _betErrorCode.value = null

                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
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
