package com.noveleta.sabongbetting

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
import com.noveleta.sabongbetting.Helper.*
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
import kotlinx.coroutines.*

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter

import kotlin.random.Random

import androidx.compose.foundation.gestures.detectTapGestures

import androidx.lifecycle.Lifecycle

class ScannerActivity : ComponentActivity() {
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
          BarcodeScannerScreen(
            onScanResult = { code ->
              setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("scanned_code", code)
              })
              finish()
            },
            onCancel = {
              setResult(Activity.RESULT_CANCELED)
              finish()
            }
          )
        }
      }
    }
  }
}

@Composable
fun BarcodeScannerScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onScanResult: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var scanCompleted by remember { mutableStateOf(false) }
    var overlayColor by remember { mutableStateOf(Color.Red) }

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE // Fixed for Android 7
        }
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember { BarcodeScanning.getClient() }

    fun cleanUpCamera() {
        try {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            scanner.close()
            cameraExecutor.shutdown()
            Log.d("BarcodeScannerScreen", "Camera resources released.")
        } catch (e: Exception) {
            Log.e("BarcodeScannerScreen", "Error releasing camera resources", e)
        }
    }

    LaunchedEffect(scanCompleted) {
        if (scanCompleted) {
            overlayColor = Color.Green
            delay(1000L)
        }
    }

    DisposableEffect(lifecycleOwner) {
    val scanner = BarcodeScanning.getClient()
    val cameraExecutor = Executors.newSingleThreadExecutor()

    val analysisUseCase = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also { useCase ->
            useCase.setAnalyzer(cameraExecutor) { imageProxy ->
                if (scanCompleted) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            barcodes.firstOrNull()?.rawValue?.let { code ->
                                if (code.isNotEmpty() && !scanCompleted) {
                                    scanCompleted = true
                                    onScanResult(code)
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.e("BarcodeScannerScreen", "Scanning failed", it)
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }
        }

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    previewView.post {
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .setTargetRotation(previewView.display.rotation)
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analysisUseCase)
                }
            } catch (e: Exception) {
                Log.e("BarcodeScannerScreen", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    onDispose {
        try {
            cameraProviderFuture.get().unbindAll()
        } catch (e: Exception) {
            Log.e("BarcodeScannerScreen", "Camera cleanup failed", e)
        }
        cameraExecutor.shutdown()
    }
}


    Box(modifier = modifier) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        ScannerOverlayBox(borderColor = overlayColor)

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Scan QRCode Receipt", color = Color.White, fontWeight = FontWeight.Bold)
                IconButton(onClick = {
                    cleanUpCamera()
                    onCancel()
                }) {
                    Icon(Icons.Default.Close, "Close Scanner", tint = Color.White)
                }
            }
        }
    }
}
