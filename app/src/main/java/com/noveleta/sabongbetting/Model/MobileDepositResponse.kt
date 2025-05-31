package com.noveleta.sabongbetting.Model;

import kotlinx.serialization.Serializable

@Serializable
data class MobileDepositResponse(
    val success: Boolean,
    val result: Int,
    val message: String,
    val barcode: String,
    val accountID: String,
    val logAction: String,
    val mobileNumber: String,
    val currentBalance: String,
    val depositAmount: String,
    val availablePoints: String,
    val datetime: String,
    val systemName: String,
    val cashier: String
)

