package com.noveleta.sabongbetting.Model

data class LoginResponse(
    val result: Int,
    val session_id: String?,
    val accountID: String?,
    val firstname: String?,
    val lastname: String?,
    val roleID: String?,
    val mobileNumber: String?,
    val drawSetting: String?,
    val drawMultiplier: String?
)
