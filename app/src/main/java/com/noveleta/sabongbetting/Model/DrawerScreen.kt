package com.noveleta.sabongbetting.Model

import androidx.annotation.DrawableRes
import com.noveleta.sabongbetting.R

sealed class DrawerScreen(
    val route: String,
    val title: String,
    @DrawableRes val iconRes: Int
) {
    object liveBet   : DrawerScreen("livebet", "Live Bet", R.drawable.ic_live)
    object placeBet : DrawerScreen("placeBet", "Place Bet", R.drawable.ic_money)
    object claimPayout : DrawerScreen("claimPayout", "Claim Payouts", R.drawable.ic_reciept)
    object cancelBet : DrawerScreen("cancelBet", "Cancel Bets", R.drawable.ic_cancel)
    object transactionLog : DrawerScreen("transactionLogs", "Current Bets", R.drawable.ic_coins)
    object withdrawDepositTicket : DrawerScreen("withdrawDeposit", "Withdraw/Deposit Ticket", R.drawable.ic_bank)
    object currentBetsLog : DrawerScreen("currentBetsLog", "Transaction Logs History", R.drawable.ic_history)
}

