package com.noveleta.sabongbetting

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

import androidx.activity.viewModels
import com.sunmi.peripheral.printer.*

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

import android.os.Build
import android.util.Log
import android.util.Size

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

import androidx.activity.result.contract.ActivityResultContracts

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

import java.util.concurrent.Executors
import androidx.compose.ui.graphics.Color

import androidx.camera.core.*

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


import com.noveleta.sabongbetting.ui.theme.MyComposeApplicationTheme
import com.noveleta.sabongbetting.*
import android.app.Activity
import android.content.Intent

import android.content.pm.PackageManager
import android.widget.Toast

import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.geometry.Rect

import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.FontWeight

import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.MeteringPointFactory

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter

import kotlin.random.Random

import androidx.compose.foundation.gestures.detectTapGestures

class MainActivity : ComponentActivity() {

    private lateinit var networkMonitor: NetworkMonitor
    private val permissionGranted = mutableStateOf(false)

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionGranted.value = true
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkMonitor = NetworkMonitor(this)

        val placeBetsViewModel: PlaceBetsViewModel by viewModels()
        val liveBettingViewModel: LiveBettingViewModel by viewModels()

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            AppLifecycleObserver(placeBetsViewModel, liveBettingViewModel)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Thread.setDefaultUncaughtExceptionHandler { _, ex ->
                Toast.makeText(
                    this,
                    "Camera error: ${ex.localizedMessage ?: "unknown"}",
                    Toast.LENGTH_LONG
                ).show()
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }

        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)

        setContent {
            MyComposeApplicationTheme {
                if (permissionGranted.value ||
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    AppNavHost(
                        networkMonitor = networkMonitor,
                        viewModelFactory = AccountLogInViewModelFactory(application)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Only show printer status, don't re-init
        if (SunmiPrinterHelper.sunmiPrinterService != null) {
            SunmiPrinterHelper.showPrinterStatus(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor.unregister()
        // Do NOT de-init printer here; handled in Application
    }
}

