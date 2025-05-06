package com.noveleta.sabongbetting.Model

data class BetResponse(
    val success: Boolean,
    val barcode: String,
    val fightNumber: Int,
    val betType: Int,
    val amount: Double,
    val transactionDate: String,
    val cashier: String,
    val systemName: String
)
