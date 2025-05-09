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
    SunmiPrinterHelper.printText("${betResponse.barcode}",        30f, true,  false, "MONOSPACE")
    SunmiPrinterHelper.printText(" ",                             30f, false, false, "MONOSPACE")
    SunmiPrinterHelper.printText("${betResponse.transactionDate}",30f, true,  false, "MONOSPACE")
    SunmiPrinterHelper.printText(" ",                             30f, false, false, "MONOSPACE")
    SunmiPrinterHelper.printText("${betResponse.systemName}",     30f, true,  false, "MONOSPACE")
    SunmiPrinterHelper.printText("Cashier: ${betResponse.cashier}",28f, true,  false, "MONOSPACE")
    SunmiPrinterHelper.printText("Bet",                           30f, true,  false, "MONOSPACE")

    // Label/value pairs
    SunmiPrinterHelper.printLabelValue("AMOUNT: ",    betResponse.amount.toString())
    SunmiPrinterHelper.printLabelValue("FIGHT #: ",   betResponse.fightNumber.toString())
    SunmiPrinterHelper.printLabelValue("SIDE: ",      when (betResponse.betType) {
        1 -> "MERON"
        2 -> "WALA"
        else -> "DRAW"
    })

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
        SunmiPrinterHelper.printText("${betResponse.barcode}", 30f, true, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 30f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.transactionDate}", 30f, true, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 30f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.systemName}", 30f, true, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 30f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("Cashier: ${betResponse.cashier}", 28f, true, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 30f, false, false, "MONOSPACE")
        // Print Title
        SunmiPrinterHelper.printText("Reprint Bet", 30f, true, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.betType)
        SunmiPrinterHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber)
        SunmiPrinterHelper.printLabelValue("SIDE: ", betResponse.fightNumber)
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
}