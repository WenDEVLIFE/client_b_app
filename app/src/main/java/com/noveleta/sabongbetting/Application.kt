package com.noveleta.sabongbetting

import com.noveleta.sabongbetting.SharedPreference.*

import android.app.Application

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
        SunmiPrinterHelper.initSunmiPrinterService(this) {
            Log.d("Printer", "Printer initialized from Application")
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        SunmiPrinterHelper.deInitSunmiPrinterService(this)
    }
}

