package com.noveleta.sabongbetting.Model;

import kotlinx.serialization.Serializable

@Serializable
data class CashoutResponse(
    val resultCode: Int,
    val message: String,
    val transactionDate: String,
    val systemName: String,
    val cashier: String,
    val transactionType: String,
    val cashoutAmount: String,
    val cashHandler: String
)

