package com.noveleta.sabongbetting.Model;

data class PlaceBetsData(
    val success: String,
    val meronTotalBetAmount: String,
    val payoutMeron: String,
    val statusMeron: String,
    val drawTotalBetAmount: String,
    val payoutDraw: String,
    val statusDraw: String,
    val walaTotalBetAmount: String,
    val payoutWala: String,
    val statusWala: String
)
