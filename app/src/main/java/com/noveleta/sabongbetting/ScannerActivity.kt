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
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember { BarcodeScanning.getClient() }

    // throttle scanning
    var lastAnalyzedMs by remember { mutableStateOf(0L) }

    // Hold a ref to the PreviewView
    var previewView by remember<PreviewView?>(mutableStateOf(null))

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    // switch to COMPATIBLE so we’re less likely to get a black screen on older devices
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Once previewView is non-null, set up CameraX binding exactly once
        DisposableEffect(previewView) {
            val providerFuture = ProcessCameraProvider.getInstance(context)
            var cameraProvider: ProcessCameraProvider? = null

            val listener = Runnable {
                cameraProvider = providerFuture.get()

                // Preview use‑case
                val previewUseCase = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView!!.surfaceProvider) }

                // Analysis use‑case
                val analysisUseCase = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setTargetResolution(Size(640, 480))
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            val now = System.currentTimeMillis()
                            if (now - lastAnalyzedMs >= 200) {
                                lastAnalyzedMs = now
                                imageProxy.image?.let { mediaImage ->
                                    val inputImage = InputImage.fromMediaImage(
                                        mediaImage, imageProxy.imageInfo.rotationDegrees
                                    )
                                    scanner.process(inputImage)
                                        .addOnSuccessListener { barcodes ->
                                            barcodes.firstOrNull()
                                                ?.rawValue
                                                ?.takeIf { it.isNotEmpty() }
                                                ?.let(onScanResult)
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } ?: imageProxy.close()
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                try {
                    cameraProvider!!.unbindAll()
                    cameraProvider!!.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        previewUseCase,
                        analysisUseCase
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Camera start failed: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    onCancel()
                }
            }

            providerFuture.addListener(listener, ContextCompat.getMainExecutor(context))

            onDispose {
                // clean up
                cameraProvider?.unbindAll()
                scanner.close()
                cameraExecutor.shutdown()
            }
        }

        // Overlay + controls
        ScannerOverlayBox()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Scan QRCode Receipt",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopStart)
            )
            IconButton(
                onClick = onCancel,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel scanning",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ScannerOverlayBox(
    modifier: Modifier = Modifier,
    boxWidth: Float = 500f,
    boxHeight: Float = 500f
) {
    var showLine by remember { mutableStateOf(true) }

    // Blinking effect: toggle every 500ms
    LaunchedEffect(Unit) {
        while (true) {
            showLine = !showLine
            delay(500)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val left = (canvasWidth - boxWidth) / 2f
            val top = (canvasHeight - boxHeight) / 2f
            val right = left + boxWidth
            val bottom = top + boxHeight

            val overlayPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#99000000") // semi-transparent black
            }

            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas

                // Save layer
                val saveLayer = nativeCanvas.saveLayer(null, null)

                // Draw full dark overlay
                nativeCanvas.drawRect(0f, 0f, canvasWidth, canvasHeight, overlayPaint)

                // Transparent cutout
                val clearPaint = android.graphics.Paint().apply {
                    xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
                }
                nativeCanvas.drawRect(left, top, right, bottom, clearPaint)

                // Blinking red line in the center
                if (showLine) {
                    val redLinePaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.RED
                        strokeWidth = 3f
                    }
                    val centerY = top + (boxHeight / 2f)
                    nativeCanvas.drawLine(left, centerY, right, centerY, redLinePaint)
                }

                nativeCanvas.restoreToCount(saveLayer)
            }
        }
    }
}
