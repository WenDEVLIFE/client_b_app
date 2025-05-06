package com.noveleta.sabongbetting

import com.noveleta.sabongbetting.SharedPreference.*

import android.app.Application

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
        
    }
}
