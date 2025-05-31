package com.noveleta.sabongbetting.Model

import kotlinx.serialization.Serializable

@Serializable
data class BetResponse(
    val success: Boolean,
    val barcode: String,
    val fightNumber: Int,
    val betType: Int,
    val amount: String,
    val transactionDate: String,
    val cashier: String,
    val systemName: String
)
