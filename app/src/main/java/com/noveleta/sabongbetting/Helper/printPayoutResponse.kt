package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*

fun printTellerCashoutResponse(context: Context, betResponse: CashoutResponse) {
    
    SunmiPrinterHelper.initSunmiPrinterService(context){
    SunmiPrinterHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right

        // Print Details
        SunmiPrinterHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("Cashier: ${betResponse.cashier}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
        SunmiPrinterHelper.printText("Cash out", 26f, true, false, "DEFAULT")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printLabelValue("Amount: ", betResponse.cashoutAmount)
        SunmiPrinterHelper.printLabelValue("Cash Handler Name: ", betResponse.cashHandler)
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
    }
}

fun printTellerCashinResponse(context: Context, betResponse: CashinResponse) {
    
    SunmiPrinterHelper.initSunmiPrinterService(context){
    SunmiPrinterHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right

        // Print Details
        SunmiPrinterHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("Cashier: ${betResponse.cashierUsername}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
        SunmiPrinterHelper.printText("Cash out", 26f, true, false, "DEFAULT")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printLabelValue("Amount: ", betResponse.cashinAmount)
        SunmiPrinterHelper.printLabelValue("Cash Handler Name: ", betResponse.cashinHandlerUsername)
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
    }
}

fun printPayout(context: Context, betResponse: BetPayoutResponse) {
    
    SunmiPrinterHelper.initSunmiPrinterService(context){
    SunmiPrinterHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right

        // Print Details
        SunmiPrinterHelper.printText("${betResponse.transactionCode}", 20f, true, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("Cashier: ${betResponse.transactionCashier}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
SunmiPrinterHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber.toString())
SunmiPrinterHelper.printLabelValue("SIDE: ", betResponse.side)
SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.amount)
SunmiPrinterHelper.printLabelValue("ODDS: ", betResponse.odds)
SunmiPrinterHelper.printLabelValue("PAYOUT: ", betResponse.payout)

        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
    }
}


