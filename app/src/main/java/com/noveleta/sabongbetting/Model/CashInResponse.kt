package com.noveleta.sabongbetting.Model;

data class CashinResponse(
    val success: Boolean,
    val message: String,
    val resultCode: Int,
    val cashinAmount: String,
    val cashinHandlerUsername: String,
    val transactionDate: String,
    val cashierUsername: String,
    val systemName: String,
    val barcode: String,
    val eventId: Int,
    val cashHandlerId: Int,
    val roleId: Int
)

