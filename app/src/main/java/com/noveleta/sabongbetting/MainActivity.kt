package com.noveleta.sabongbetting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

import androidx.activity.viewModels

import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.ui.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.Network.*
import com.noveleta.sabongbetting.Enter.*
import com.noveleta.sabongbetting.*

class MainActivity : ComponentActivity() {
    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkMonitor = NetworkMonitor(this)
        val placeBetsViewModel: PlaceBetsViewModel by viewModels()
        val liveBettingViewModel: LiveBettingViewModel by viewModels()

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            AppLifecycleObserver(placeBetsViewModel, liveBettingViewModel)
        )

        setContent {
            MyComposeApplicationTheme {
                AppNavHost(
                    networkMonitor = networkMonitor,
                    viewModelFactory = AccountLogInViewModelFactory(application)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor.unregister()
    }
}
