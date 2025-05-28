package com.noveleta.sabongbetting

import android.app.Application
import com.noveleta.sabongbetting.SharedPreference.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
        // Printer initialization is now handled in MainActivity
    }
}


