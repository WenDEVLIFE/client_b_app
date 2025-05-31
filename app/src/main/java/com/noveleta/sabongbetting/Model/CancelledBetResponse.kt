package com.noveleta.sabongbetting.Model;

import kotlinx.serialization.Serializable

@Serializable
data class CancelledBetResponse(
    val barcode: String,
    val dateTime: String,
    val systemName: String,
    val cashier: String,
    val fightNumber: String,
    val side: String,
    val amount: String,
    val logAction: String
)
