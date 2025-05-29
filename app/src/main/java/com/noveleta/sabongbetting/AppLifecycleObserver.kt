package com.noveleta.sabongbetting

import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.ui.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.Network.*
import com.noveleta.sabongbetting.Enter.*
import com.noveleta.sabongbetting.*

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver(private val viewModel: PlaceBetsViewModel, private val viewModelTwo: LiveBettingViewModel) : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        // App went to background or stopped
    }
}
