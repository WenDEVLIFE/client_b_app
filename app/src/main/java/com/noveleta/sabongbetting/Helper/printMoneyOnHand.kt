package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*
import android.widget.Toast

fun printMoneyOnHand(context: Context, betResponse: SummaryReport) {
    
    if (!SunmiPrinterHelper.isPrinterReady()) {
        Toast.makeText(context, "Printer not ready", Toast.LENGTH_SHORT).show()
        return
    }

        SunmiPrinterHelper.setAlign(0) // 0 = Left, 1 = Center, 2 = Right
        
        // Print Details
        SunmiPrinterHelper.print3Line()
        SunmiPrinterHelper.printLabelValue("","SUMMARY REPORT")
        SunmiPrinterHelper.printLabelValue("","(Money On Hand)")
        SunmiPrinterHelper.printLabelValue("","${betResponse.systemName}")
        SunmiPrinterHelper.printLabelValue("${betResponse.dateTime}","")
        SunmiPrinterHelper.printLabelValue("", "Username: ${betResponse.username}")
        
        SunmiPrinterHelper.print3Line()
        
        SunmiPrinterHelper.printLabelValue("Cash In: ${betResponse.cashIn}", "")
        SunmiPrinterHelper.printLabelValue("Total bets: ${betResponse.totalBets}", "")
        SunmiPrinterHelper.printLabelValue("Total mobile deposit: ${betResponse.totalMobileDeposit}", "")
        
        SunmiPrinterHelper.print3Line()
        
        SunmiPrinterHelper.printLabelValue("Cash Out: ${betResponse.cashOut}", "")
        SunmiPrinterHelper.printLabelValue("Total payout paid: ${betResponse.totalPayoutPaid}", "")
        SunmiPrinterHelper.printLabelValue("Total cancelled paid: ${betResponse.totalCancelledPaid}", "")
        SunmiPrinterHelper.printLabelValue("Total cancelled bet: ${betResponse.totalCancelledBet}", "")
        SunmiPrinterHelper.printLabelValue("Total draw bets Cancelled paid: ${betResponse.totalDrawBetsCancelledPaid }", "")
        SunmiPrinterHelper.printLabelValue("Total draw paid: ${betResponse.totalDrawPaid}", "")
        SunmiPrinterHelper.printLabelValue("Total mobile withdraw: ${betResponse.totalMobileWithdraw}", "")
        
        SunmiPrinterHelper.print3Line()
        
        SunmiPrinterHelper.printLabelValue("", "Money On Hand: ${betResponse.moneyOnHand}")
        if(betResponse.commisionBody == "0"){
           SunmiPrinterHelper.print3Line()
        }else{
           SunmiPrinterHelper.printLabelValue("", "Commission: ${betResponse.commisionBody}")
        }
        
        SunmiPrinterHelper.printLabelValue("Total Payout Unclaimed:", "")
        SunmiPrinterHelper.printLabelValue("${betResponse.totalPayoutUnclaimed}", "")
        SunmiPrinterHelper.printLabelValue("Total draw unclaimed: ${betResponse.totalDrawUnclaimed}", "")
        SunmiPrinterHelper.printLabelValue("----------------------", "")
        SunmiPrinterHelper.printLabelValue("DRAW REPORTS", "")
        SunmiPrinterHelper.printLabelValue("----------------------", "")
        SunmiPrinterHelper.printLabelValue("Total draw bets: ${betResponse.totalDrawBets}", "")
        SunmiPrinterHelper.printLabelValue("Total draw bets paid: ${betResponse.totalDrawBetsPaid}", "")
        SunmiPrinterHelper.printLabelValue("Total draw bets Cancelled: ", "")
        SunmiPrinterHelper.printLabelValue("${betResponse.totalDrawBetsCancelled}", "")
        SunmiPrinterHelper.printLabelValue("Total draw payout unclaimed: ", "")
        SunmiPrinterHelper.printLabelValue("${betResponse.totalDrawPayoutUnclaimed}", "")

        SunmiPrinterHelper.print3Line()
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
}
