package com.noveleta.sabongbetting.Helper

import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.SharedPreference.*
import com.noveleta.sabongbetting.Model.*

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.content.Context
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject


class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    var userName = mutableStateOf("")
    var password = mutableStateOf("")

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    fun logInUser() {
    _loginState.value = LoginState.Loading

    RetrofitClient.checkUserApi.checkUser(
    androidLogin = "true", 
    userName.value, 
    password.value
    ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    try {
                        val jsonObject = JSONObject(json)
                        val resultCode = jsonObject.optInt("result", -1)

                        // Save session only if login is valid
                        if (resultCode == 1 || resultCode == 2) {
                            with(SessionManager) {
                                sessionId = jsonObject.optString("session_id")
                                saveUserData(
                                    accountID = jsonObject.optString("accountID"),
                                    cname = jsonObject.optString("cname"),
                                    roleID = jsonObject.optString("roleID"),
                                    betTypeId = jsonObject.optString("betTypeId"),
                                    oddsSettings = jsonObject.optString("oddsSettings"),
                                    specialTeller = jsonObject.optString("specialTeller"),
                                    payoutSettings = jsonObject.optString("payoutSettings")
                                )
                            }
                        }

                        _loginState.value = LoginState.Success(resultCode)
                    } catch (e: Exception) {
                        _loginState.value = LoginState.Error("Invalid server response.")
                    }
                } else {
                    _loginState.value = LoginState.Error("Login failed. Try again.")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _loginState.value = LoginState.Error("Error: ${t.localizedMessage}")
            }
        })
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
