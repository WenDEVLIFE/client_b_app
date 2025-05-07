package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*

fun printCancelledBetting(context: Context, betResponse: CancelledBetResponse) {


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
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.dateTime}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("Cashier: ${betResponse.cashier}", 18f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        
        // Print Title
        SunmiPrinterHelper.printText("CANCELLED TICKET BET", 26f, false, false, "DEFAULT")
        
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printLabelValue("Fight #: ", betResponse.fightNumber)
        SunmiPrinterHelper.printLabelValue("SIDE: ", betResponse.side)
        SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.amount)
        
        }
}


