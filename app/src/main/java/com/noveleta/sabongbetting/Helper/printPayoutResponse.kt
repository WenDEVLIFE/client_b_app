package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*

fun printTellerCashoutResponse(context: Context, betResponse: CashoutResponse) {
    val printerHelper = SunmiPrinterHelper()
    printerHelper.initSunmiPrinterService(context){
    printerHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right

        // Print Details
        printerHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("Cashier: ${betResponse.cashier}", 20f, false, false, "MONOSPACE")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
        printerHelper.printText("Cash out", 26f, true, false, "DEFAULT")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        printerHelper.printLabelValue("Amount: ", betResponse.cashoutAmount)
        printerHelper.printLabelValue("Cash Handler Name: ", betResponse.cashHandler)
        
        // Cut paper and feed
        printerHelper.cutpaper()
        printerHelper.feedPaper()
    }
}

fun printTellerCashinResponse(context: Context, betResponse: CashinResponse) {
    val printerHelper = SunmiPrinterHelper()
    printerHelper.initSunmiPrinterService(context){
    printerHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right

        // Print Details
        printerHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("Cashier: ${betResponse.cashierUsername}", 20f, false, false, "MONOSPACE")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
        printerHelper.printText("Cash out", 26f, true, false, "DEFAULT")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        printerHelper.printLabelValue("Amount: ", betResponse.cashinAmount)
        printerHelper.printLabelValue("Cash Handler Name: ", betResponse.cashinHandlerUsername)
        
        // Cut paper and feed
        printerHelper.cutpaper()
        printerHelper.feedPaper()
    }
}

fun printPayout(context: Context, betResponse: BetPayoutResponse) {
    val printerHelper = SunmiPrinterHelper()
    printerHelper.initSunmiPrinterService(context){
    printerHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right

        // Print Details
        printerHelper.printText("${betResponse.transactionCode}", 20f, true, false, "MONOSPACE")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        printerHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("Cashier: ${betResponse.transactionCashier}", 20f, false, false, "MONOSPACE")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
printerHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber.toString())
printerHelper.printLabelValue("SIDE: ", betResponse.side)
printerHelper.printLabelValue("AMOUNT: ", betResponse.amount)
printerHelper.printLabelValue("ODDS: ", betResponse.odds)
printerHelper.printLabelValue("PAYOUT: ", betResponse.payout)

        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        
        // Cut paper and feed
        printerHelper.cutpaper()
        printerHelper.feedPaper()
    }
}


