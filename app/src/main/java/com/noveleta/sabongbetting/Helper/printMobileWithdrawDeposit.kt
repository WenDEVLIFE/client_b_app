package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*

fun printMobileWithdraw(context: Context, betResponse: MobileWithdrawResponse) {

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
        SunmiPrinterHelper.printText("${betResponse.timestamp}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("Cashier: ${betResponse.cashier}", 18f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        
        // Print Title
        SunmiPrinterHelper.printText("POINTS WITHDRAWN", 26f, false, false, "DEFAULT")
        
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printLabelValue("ACCOUNT: ", betResponse.accountID)
        SunmiPrinterHelper.printLabelValue("CURRENT POINTS: ", betResponse.currentBalance)
        SunmiPrinterHelper.printLabelValue("WITHDRAW AMOUNT: ", betResponse.withdrawnAmount)
        SunmiPrinterHelper.printLabelValue("AVAILABLE POINTS: ", betResponse.newBalance)
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
    }
}


fun printMobileDeposit(context: Context, betResponse: MobileDepositResponse) {


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
        SunmiPrinterHelper.printText("${betResponse.datetime}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("${betResponse.systemName}", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText("Cashier: ${betResponse.cashier}", 18f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        
        // Print Title
        SunmiPrinterHelper.printText("POINTS DEPOSITED", 26f, false, false, "DEFAULT")
        
        SunmiPrinterHelper.printText(" ", 20f, false, false, "MONOSPACE")
        SunmiPrinterHelper.printLabelValue("ACCOUNT: ", betResponse.accountID)
        SunmiPrinterHelper.printLabelValue("CURRENT POINTS: ", betResponse.currentBalance)
        SunmiPrinterHelper.printLabelValue("DEPOSIT AMOUNT: ", betResponse.depositAmount)
        SunmiPrinterHelper.printLabelValue("AVAILABLE POINTS: ", betResponse.availablePoints)
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
    }
}

