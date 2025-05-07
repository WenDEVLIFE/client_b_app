package com.noveleta.sabongbetting.Helper

import android.content.Context
import com.noveleta.sabongbetting.Model.*

fun printBetResponse(context: Context, betResponse: BetResponse) {
    val printerHelper = SunmiPrinterHelper()
    printerHelper.initSunmiPrinterService(context){
    printerHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right
        
    // Print Barcode using printer's native method
        printerHelper.printQr(
           data = betResponse.barcode,
           modulesize = 8,    // size of each QR “dot” (1–16)
           errorlevel = 2     // error correction level (0=L, 1=M, 2=Q, 3=H)
        )
        
        // Print Details
        printerHelper.printText("${betResponse.barcode}", 20f, true, false, "MONOSPACE")
        printerHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("Cashier: ${betResponse.cashier}", 18f, false, false, "MONOSPACE")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
        printerHelper.printText("Bet", 26f, false, false, "DEFAULT")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        printerHelper.printLabelValue("AMOUNT: ", betResponse.amount.toString())
        printerHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber.toString())
        if(betResponse.betType == 1){
         printerHelper.printLabelValue("SIDE: ", "MERON")
                }else if(betResponse.betType == 2){
                 printerHelper.printLabelValue("SIDE: ", "WALA")
                }else{
                printerHelper.printLabelValue("SIDE: ", "DRAW")
                }
        
        // Cut paper and feed
        printerHelper.cutpaper()
        printerHelper.feedPaper()
        
    }
}

fun rePrintBetResponse(context: Context, betResponse: ReprintBetResponse) {
    val printerHelper = SunmiPrinterHelper()
    printerHelper.initSunmiPrinterService(context){
    printerHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right
        
    // Print Barcode using printer's native method
        printerHelper.printQr(
           data = betResponse.barcode,
           modulesize = 8,    // size of each QR “dot” (1–16)
           errorlevel = 2     // error correction level (0=L, 1=M, 2=Q, 3=H)
        )
        
        // Print Details
        printerHelper.printText("${betResponse.barcode}", 20f, true, false, "MONOSPACE")
        printerHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("Cashier: ${betResponse.cashier}", 18f, false, false, "MONOSPACE")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
        printerHelper.printText("Reprint Bet", 26f, false, false, "DEFAULT")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        printerHelper.printLabelValue("AMOUNT: ", betResponse.betType)
        printerHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber)
        printerHelper.printLabelValue("SIDE: ", betResponse.fightNumber)
        
        // Cut paper and feed
        printerHelper.cutpaper()
        printerHelper.feedPaper()
    }
}