package com.noveleta.sabongbetting.Model;

sealed class LoginState {
    object Idle        : LoginState()
    object Loading     : LoginState()
    data class Success(val code: Int) : LoginState()
    data class Error(val message: String) : LoginState()
}

