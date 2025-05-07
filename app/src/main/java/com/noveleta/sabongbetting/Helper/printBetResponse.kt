package com.noveleta.sabongbetting.Helper

import android.content.Context
import com.noveleta.sabongbetting.Model.*

fun printBetResponse(context: Context, betResponse: BetResponse) {
    SunmiPrinterHelper.initSunmiPrinterService(context){
    SunmiPrinterHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right
        
    // Print Barcode using printer's native method
        SunmiPrinterHelper.printQr(
           data = betResponse.barcode,
           modulesize = 8,    // size of each QR “dot” (1–16)
           errorlevel = 2     // error correction level (0=L, 1=M, 2=Q, 3=H)
        )
        
        // Print Details
        SunmiPrinterHelper.printText("${betResponse.barcode}", 20f, true, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("Cashier: ${betResponse.cashier}", 18f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
        SunmiPrinterHelper.printText("Bet", 26f, false, false, "DEFAULT")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.amount.toString())
        SunmiPrinterHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber.toString())
        if(betResponse.betType == 1){
         SunmiPrinterHelper.printLabelValue("SIDE: ", "MERON")
                }else if(betResponse.betType == 2){
                 SunmiPrinterHelper.printLabelValue("SIDE: ", "WALA")
                }else{
                SunmiPrinterHelper.printLabelValue("SIDE: ", "DRAW")
                }
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
        
    }
}

fun rePrintBetResponse(context: Context, betResponse: ReprintBetResponse) {
    SunmiPrinterHelper.initSunmiPrinterService(context){
    SunmiPrinterHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right
        
    // Print Barcode using printer's native method
        SunmiPrinterHelper.printQr(
           data = betResponse.barcode,
           modulesize = 8,    // size of each QR “dot” (1–16)
           errorlevel = 2     // error correction level (0=L, 1=M, 2=Q, 3=H)
        )
        
        // Print Details
        SunmiPrinterHelper.printText("${betResponse.barcode}", 20f, true, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.transactionDate}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("Cashier: ${betResponse.cashier}", 18f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        // Print Title
        SunmiPrinterHelper.printText("Reprint Bet", 26f, false, false, "DEFAULT")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.betType)
        SunmiPrinterHelper.printLabelValue("FIGHT #: ", betResponse.fightNumber)
        SunmiPrinterHelper.printLabelValue("SIDE: ", betResponse.fightNumber)
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
    }
}