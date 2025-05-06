package com.noveleta.sabongbetting.Model;

data class ReprintBetResponse(
    val barcode: String,
    val fightNumber: String,
    val betType: String,
    val amount: String,
    val transactionDate: String,
    val cashier: String,
    val systemName: String
)

