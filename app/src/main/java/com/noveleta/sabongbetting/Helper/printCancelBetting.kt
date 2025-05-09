package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*
import android.widget.Toast

fun printCancelledBetting(context: Context, betResponse: CancelledBetResponse) {
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
    
        SunmiPrinterHelper.printLabelValue("${betResponse.dateTime}","")
        SunmiPrinterHelper.printLabelValue("${betResponse.systemName}","")
        
        SunmiPrinterHelper.printLabelValue("Cashier: ", betResponse.cashier)
        SunmiPrinterHelper.printLabelValue("CANCELLED TICKET BET", "")
        // Print Title
        
        SunmiPrinterHelper.printLabelValue("Fight #: ", betResponse.fightNumber)
        SunmiPrinterHelper.printLabelValue("SIDE: ", betResponse.side)
        SunmiPrinterHelper.printLabelValue("AMOUNT: ", betResponse.amount)
        SunmiPrinterHelper.print3Line()
        
        SunmiPrinterHelper.cutpaper()
    SunmiPrinterHelper.feedPaper()
}


