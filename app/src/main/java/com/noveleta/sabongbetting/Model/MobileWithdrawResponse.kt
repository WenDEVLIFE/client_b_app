package com.noveleta.sabongbetting.Model

data class MobileWithdrawResponse(
    val success: Boolean,
    val message: String,
    val barcode: String,
    val accountID: String,
    val mobileNumber: String,
    val withdrawnAmount: String,
    val previousBalance: String,
    val newBalance: String,
    val timestamp: String,
    val cashier: String,
    val systemName: String,
    val logAction: String,
    val currentBalance: String
)

