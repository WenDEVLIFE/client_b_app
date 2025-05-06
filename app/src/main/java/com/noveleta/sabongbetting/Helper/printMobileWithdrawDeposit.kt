package com.noveleta.sabongbetting.Helper;

import android.content.Context
import com.noveleta.sabongbetting.Model.*

fun printMobileWithdraw(context: Context, betResponse: MobileWithdrawResponse) {

val printerHelper = SunmiPrinterHelper()
    printerHelper.initSunmiPrinterService(context)

    try {
        printerHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right
        
        // Cut paper and feed
        printerHelper.cutpaper()
        printerHelper.feedPaper()

    } catch (e: Exception) {
        printerHelper.showPrinterStatus(context)
        //Toast.makeText(context, "Printing failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}


fun printMobileDeposit(context: Context, betResponse: MobileDepositResponse) {

val printerHelper = SunmiPrinterHelper()
    printerHelper.initSunmiPrinterService(context)

    try {
        printerHelper.setAlign(1) // 0 = Left, 1 = Center, 2 = Right
        
        // Cut paper and feed
        printerHelper.cutpaper()
        printerHelper.feedPaper()

    } catch (e: Exception) {
        printerHelper.showPrinterStatus(context)
        //Toast.makeText(context, "Printing failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}

