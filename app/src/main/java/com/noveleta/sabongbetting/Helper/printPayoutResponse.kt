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
        SunmiPrinterHelper.printLabelValue("-------- Cash Out --------", "")
        SunmiPrinterHelper.print3Line()
        
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
        
        SunmiPrinterHelper.printLabelValue("-------- Cash In --------", "")
        SunmiPrinterHelper.print3Line()
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
SunmiPrinterHelper.printQr(
           data = betResponse.transactionCode,
           modulesize = 8,    // size of each QR “dot” (1–16)
           errorlevel = 2     // error correction level (0=L, 1=M, 2=Q, 3=H)
        )
        // Print Details
        SunmiPrinterHelper.printLabelValue("${betResponse.transactionCode}","")
        SunmiPrinterHelper.print3Line()
    
        SunmiPrinterHelper.printLabelValue("${betResponse.transactionDate}","")
        SunmiPrinterHelper.printLabelValue("${betResponse.systemName}","")
        
        SunmiPrinterHelper.printLabelValue("Cashier: ", betResponse.transactionCashier)
        SunmiPrinterHelper.printLabelValue( when (betResponse.status) {
                      "REFUNDED" -> "REFUNDED"
                    "CLAIMED" -> "CLAIMED"
                    else -> "CLAIMED"
        }, "")
        SunmiPrinterHelper.print3Line()
        if(betResponse.status == "REFUNDED"){
        SunmiPrinterHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber.toString())
SunmiPrinterHelper.printLabelValue("SIDE: ", betResponse.side)
SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.amount)
        }else if (betResponse.status == "CLAIMED"){
        SunmiPrinterHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber.toString())
SunmiPrinterHelper.printLabelValue("SIDE: ", betResponse.side)
SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.amount)
SunmiPrinterHelper.printLabelValue("ODDS: ", betResponse.odds)
SunmiPrinterHelper.printLabelValue("PAYOUT: ", betResponse.payout)
        }
        // Print Title




SunmiPrinterHelper.print3Line()
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
}


