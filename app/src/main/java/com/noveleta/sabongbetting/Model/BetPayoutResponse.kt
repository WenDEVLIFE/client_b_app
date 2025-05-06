package com.noveleta.sabongbetting.Model;

data class BetPayoutResponse(
    val success: Boolean,
    val transactionCode: String,
    val transactionFightNumber: Int,
    val transactionSide: String,
    val transactionAmount: String,
    val transactionType: String,
    val transactionDate: String,
    val transactionCashier: String,
    val transactionSystemName: String,
    val transactionOddsMeron: String,
    val transactionOddsWala: String,
    val transactionPayout: String,
    val betType: Int,
    val type: String,
    val roleID: String,
    val userID: String,
    val ticketLogo: String,
    val barcode: String,
    val dateTime: String,
    val systemName: String,
    val cashier: String,
    val status: String,
    val fightNumber: Int,
    val side: String,
    val amount: String,
    val odds: String,
    val payout: String
)

