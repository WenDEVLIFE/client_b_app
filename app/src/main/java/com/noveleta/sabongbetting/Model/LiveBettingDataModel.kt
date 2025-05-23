package com.noveleta.sabongbetting.Model;

data class LiveBettingData(
    val success: String,
    val bannerName: String?,
    val promoterName: String?,
    val promoterFontSize: String?,
    val hiddenBetFightNumber: String?,
    val hiddenBetFightID: String?,
    val walaText: String?,
    val meronText: String?,
    val walaTotalBetAmount: String?,
    val meronTotalBetAmount: String?,
    val fightNumber: String?,
    val fightStatus: String?,
    val isBetting: String?,
    val isBettingWinner: String?,
    val drawTotalBetAmount: String?,
    val drawText: String?,
    val drawTotalBetAmount1: String?,
    val meronClosed: String?,
    val walaClosed: String?,
    val fights: List<Fight>?,
    val fightHistory: List<FightHistoryEntry>?,
    val cashHandlerNames: List<CashHandlers>?,
    val userTransactionLogs: List<FightLogEntry>?,
    val userCurrentBets: List<CurrentBetLogs>?,
    val drawSettingData: String?,
    val drawMultiplierData: String?,
    val drawMaxData: String?
)

    data class CashHandlers(
        val name: String,
        val id: Int
    )
    
    data class Fight(
        val number: String,
        val winner: Int,
        val betting: Int,
        val status: String
    )
    
    data class FightResult(
        val symbol: String,
        val status: String,
        val isWinner: Int?,
        val isBetting: Int?
    )
    
    data class FightHistoryEntry(
        val fightNumber: String,
        val result: FightResult
    )
    
    data class FightLogEntry(
    val transaction: String,
    val amount: String,
    val eventDate: String,
    val transactionCode: String,
    val totalBetAmount: String
    )
    
    data class CurrentBetLogs(
    val date: String,          // Formatted as "MMM dd, yyyy"
    val teller: String,
    val fightNumber: String,
    val bettor: String,        // Cashier name
    val betUnder: String,      // betTypeStatus
    val amount: String,        // Formatted with commas
    val status: String,
    val result: String,
    val isClaimed: String,     // "YES" or "NO"
    val isReturned: String     // "RETURNED" or "-"
    )


