package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*
import android.widget.Toast

fun printTellerCashoutResponse(context: Context, betResponse: CashoutResponse) {
    
    if (!SunmiPrinterHelper.isPrinterReady()) {
        Toast.makeText(context, "Printer not ready", Toast.LENGTH_SHORT).show()
        return
    }

    SunmiPrinterHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right

        // Print Details
        SunmiPrinterHelper.print3Line()
    
        SunmiPrinterHelper.printLabelValue("${betResponse.transactionDate}","")
        SunmiPrinterHelper.printLabelValue("${betResponse.systemName}","")
        
        SunmiPrinterHelper.printLabelValue("Cashier: ", betResponse.cashier)
        SunmiPrinterHelper.printLabelValue("Cash Out", "")
        
        
        // Print Title
        SunmiPrinterHelper.printLabelValue("Amount: ", betResponse.cashoutAmount)
        SunmiPrinterHelper.printLabelValue("Cash Handler Name: ", betResponse.cashHandler)
        SunmiPrinterHelper.print3Line()
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
}

fun printTellerCashinResponse(context: Context, betResponse: CashinResponse) {
    
    if (!SunmiPrinterHelper.isPrinterReady()) {
        Toast.makeText(context, "Printer not ready", Toast.LENGTH_SHORT).show()
        return
    }

    SunmiPrinterHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right

        // Print Details
        SunmiPrinterHelper.print3Line()
    
        SunmiPrinterHelper.printLabelValue("${betResponse.transactionDate}","")
        SunmiPrinterHelper.printLabelValue("${betResponse.systemName}","")
        
        SunmiPrinterHelper.printLabelValue("Cashier: ", betResponse.cashierUsername)
        SunmiPrinterHelper.printLabelValue("Cash In", "")
        
        SunmiPrinterHelper.printLabelValue("Amount: ", betResponse.cashinAmount)
        SunmiPrinterHelper.printLabelValue("Cash Handler Name: ", betResponse.cashinHandlerUsername)
        SunmiPrinterHelper.print3Line()
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
}

fun printPayout(context: Context, betResponse: BetPayoutResponse) {
    
    if (!SunmiPrinterHelper.isPrinterReady()) {
        Toast.makeText(context, "Printer not ready", Toast.LENGTH_SHORT).show()
        return
    }

    SunmiPrinterHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right

        // Print Details
        SunmiPrinterHelper.printLabelValue("${betResponse.transactionCode}","")
        SunmiPrinterHelper.print3Line()
    
        SunmiPrinterHelper.printLabelValue("${betResponse.transactionDate}","")
        SunmiPrinterHelper.printLabelValue("${betResponse.systemName}","")
        
        SunmiPrinterHelper.printLabelValue("Cashier: ", betResponse.transactionCashier)
        SunmiPrinterHelper.printLabelValue("Payout", "")
        
        // Print Title
SunmiPrinterHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber.toString())
SunmiPrinterHelper.printLabelValue("SIDE: ", betResponse.side)
SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.amount)
SunmiPrinterHelper.printLabelValue("ODDS: ", betResponse.odds)
SunmiPrinterHelper.printLabelValue("PAYOUT: ", betResponse.payout)

SunmiPrinterHelper.print3Line()
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
}


