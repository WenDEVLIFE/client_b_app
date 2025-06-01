package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*
import android.widget.Toast

fun printMobileWithdraw(context: Context, betResponse: MobileWithdrawResponse) {

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
    
        SunmiPrinterHelper.printLabelValue("${betResponse.timestamp}","")
        SunmiPrinterHelper.printLabelValue("${betResponse.systemName}","")
        
        SunmiPrinterHelper.printLabelValue("Cashier: ", betResponse.cashier)
        SunmiPrinterHelper.printLabelValue("POINTS WITHDRAWN", "")
        SunmiPrinterHelper.print3Line()
        
        SunmiPrinterHelper.printLabelValue("ACCOUNT: ", betResponse.accountID)
        SunmiPrinterHelper.printLabelValue("CURRENT POINTS: ", betResponse.currentBalance)
        SunmiPrinterHelper.printLabelValue("WITHDRAW AMOUNT: ", betResponse.withdrawnAmount)
        SunmiPrinterHelper.printLabelValue("AVAILABLE POINTS: ", betResponse.newBalance)
        
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
}


fun printMobileDeposit(context: Context, betResponse: MobileDepositResponse) {

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
    
        SunmiPrinterHelper.printLabelValue("${betResponse.datetime}","")
        SunmiPrinterHelper.printLabelValue("${betResponse.systemName}","")
        
        SunmiPrinterHelper.printLabelValue("Cashier: ", betResponse.cashier)
        SunmiPrinterHelper.printLabelValue("POINTS DEPOSITED", "")
        SunmiPrinterHelper.print3Line()
        
        SunmiPrinterHelper.printLabelValue("ACCOUNT: ", betResponse.accountID)
        SunmiPrinterHelper.printLabelValue("CURRENT POINTS: ", betResponse.currentBalance)
        SunmiPrinterHelper.printLabelValue("DEPOSIT AMOUNT: ", betResponse.depositAmount)
        SunmiPrinterHelper.printLabelValue("AVAILABLE POINTS: ", betResponse.availablePoints)
        SunmiPrinterHelper.print3Line()
        // Cut paper and feed
        SunmiPrinterHelper.cutpaper()
        SunmiPrinterHelper.feedPaper()
}

