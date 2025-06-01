package com.noveleta.sabongbetting.Helper

import android.content.Context
import com.noveleta.sabongbetting.Model.*
import android.widget.Toast

fun printBetResponse(context: Context, betResponse: BetResponse) {
    if (!SunmiPrinterHelper.isPrinterReady()) {
        Toast.makeText(context, "Printer not ready", Toast.LENGTH_SHORT).show()
        return
    }

    SunmiPrinterHelper.setAlign(1) // Center

    // QR code
    SunmiPrinterHelper.printQr(
        data       = betResponse.barcode,
        modulesize = 8,
        errorlevel = 2
    )

    // Text details
    SunmiPrinterHelper.printLabelValue("${betResponse.barcode}","")
    SunmiPrinterHelper.print3Line()
    
    SunmiPrinterHelper.printLabelValue("${betResponse.transactionDate}","")
    SunmiPrinterHelper.printLabelValue("${betResponse.systemName}","")
    SunmiPrinterHelper.printLabelValue("Cashier: ",    betResponse.cashier)
    SunmiPrinterHelper.printLabelValue("BET", "")
    SunmiPrinterHelper.print3Line()
    // Label/value pairs
    SunmiPrinterHelper.printLabelValue("AMOUNT: ",    betResponse.amount)
    SunmiPrinterHelper.printLabelValue("FIGHT #: ",   betResponse.fightNumber.toString())
    SunmiPrinterHelper.printLabelValue("SIDE: ",      when (betResponse.betType) {
        1 -> "MERON"
        2 -> "WALA"
        else -> "DRAW"
    })
SunmiPrinterHelper.print3Line()
    // Finish
    SunmiPrinterHelper.cutpaper()
    SunmiPrinterHelper.feedPaper()
}


fun rePrintBetResponse(context: Context, betResponse: ReprintBetResponse) {
    if (!SunmiPrinterHelper.isPrinterReady()) {
        Toast.makeText(context, "Printer not ready", Toast.LENGTH_SHORT).show()
        return
    }

    SunmiPrinterHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right
        
    // Print Barcode using printer's native method
        SunmiPrinterHelper.printQr(
           data = betResponse.barcode,
           modulesize = 8,    // size of each QR “dot” (1–16)
           errorlevel = 2     // error correction level (0=L, 1=M, 2=Q, 3=H)
        )
        
        // Print Details
        SunmiPrinterHelper.printLabelValue("${betResponse.barcode}","")
        SunmiPrinterHelper.print3Line()
    
        SunmiPrinterHelper.printLabelValue("${betResponse.transactionDate}","")
        SunmiPrinterHelper.printLabelValue("${betResponse.systemName}","")
        
        SunmiPrinterHelper.printLabelValue("Cashier: ", betResponse.cashier)
        SunmiPrinterHelper.printLabelValue("REPRINT BET", "")
        SunmiPrinterHelper.print3Line()
        SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.amount)
        SunmiPrinterHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber)
        SunmiPrinterHelper.printLabelValue("SIDE: ", betResponse.betType)
        SunmiPrinterHelper.print3Line()
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
}