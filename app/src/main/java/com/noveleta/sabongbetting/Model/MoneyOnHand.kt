package com.noveleta.sabongbetting.Model;

import kotlinx.serialization.Serializable

@Serializable
data class SummaryReport(
    val errorCode: Int,
    val reportTitle: String,
    val systemName: String,
    val dateTime: String,
    val username: String,
    val cashIn: String,
    val totalBets: String,
    val totalMobileDeposit: String,
    val cashOut: String,
    val totalPayoutPaid: String,
    val totalCancelledPaid: String,
    val totalCancelledBet: String,
    val totalDrawUnpaid: String,
    val totalDrawPaid: String,
    val totalDrawBets: String,
    val totalDrawBetsPaid: String,
    val totalMobileWithdraw: String,
    val moneyOnHand: String,
    val commisionBody: String,
    val commSettings: String,
    val totalPayoutUnclaimed: String,
    val totalCancelledUnclaimed: String,
    val totalDrawBetsCancelledPaid: String,
    val totalDrawBetsCancelled: String,
    val totalDrawPayoutUnclaimed: String
)

