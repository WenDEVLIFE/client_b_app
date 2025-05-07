package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*

fun printCancelledBetting(context: Context, betResponse: CancelledBetResponse) {

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
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        printerHelper.printText("${betResponse.dateTime}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        printerHelper.printText("Cashier: ${betResponse.cashier}", 18f, false, false, "MONOSPACE")
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        
        // Print Title
        printerHelper.printText("CANCELLED TICKET BET", 26f, false, false, "DEFAULT")
        
        printerHelper.printText(" ", 20f, false, false, "MONOSPACE")
        printerHelper.printLabelValue("Fight #: ", betResponse.fightNumber)
        printerHelper.printLabelValue("SIDE: ", betResponse.side)
        printerHelper.printLabelValue("AMOUNT: ", betResponse.amount)
        
        }
}


