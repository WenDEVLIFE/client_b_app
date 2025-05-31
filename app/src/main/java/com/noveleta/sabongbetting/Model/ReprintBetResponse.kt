package com.noveleta.sabongbetting.Model;

import kotlinx.serialization.Serializable

@Serializable
data class ReprintBetResponse(
    val barcode: String,
    val fightNumber: String,
    val betType: String,
    val amount: String,
    val transactionDate: String,
    val cashier: String,
    val systemName: String
)

