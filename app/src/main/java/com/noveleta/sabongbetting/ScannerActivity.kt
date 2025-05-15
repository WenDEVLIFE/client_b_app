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

    // ML Kit scanner
    val scanner = remember { BarcodeScanning.getClient() }
    // Camera executor
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // PreviewView to show camera feed
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // 1) Preview use-case
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // 2) ImageAnalysis use-case
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { useCase ->
                        useCase.setAnalyzer(cameraExecutor) { imageProxy ->
                            // Convert to ML Kit's InputImage
                            imageProxy.image
                                ?.let { mediaImage ->
                                    InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )
                                }
                                ?.let { inputImage ->
                                    scanner.process(inputImage)
                                        .addOnSuccessListener { barcodes ->
                                            barcodes.firstOrNull()
                                                ?.rawValue
                                                ?.takeIf { it.isNotEmpty() }
                                                ?.also { code ->
                                                    onScanResult(code)
                                                }
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } ?: imageProxy.close()
                        }
                    }

                // 3) Bind to lifecycle
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    analysis
                )
            }, ContextCompat.getMainExecutor(context))
        }
    )
    
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
                onClick = {
                // shut down scanner to free resources
                scanner.close()
                cameraExecutor.shutdown()
                onCancel()
            },
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


@Composable
fun ScannerOverlayBox(
    modifier: Modifier = Modifier,
    boxWidth: Dp = 250.dp,
    boxHeight: Dp = 250.dp
) {
    // blinking state
    var showLine by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            showLine = !showLine
        }
    }

    // Convert Dp to pixels inside Canvas
    Box(
        modifier
            .fillMaxSize()
            .drawWithContent {
                // 1) draw what’s underneath (camera)
                drawContent()

                val canvasWidth = size.width
                val canvasHeight = size.height
                val boxW = boxWidth.toPx()
                val boxH = boxHeight.toPx()
                val left = (canvasWidth - boxW) / 2f
                val top = (canvasHeight - boxH) / 2f
                val right = left + boxW
                val bottom = top + boxH
                val centerY = top + boxH / 2f

                // 2) darken the whole screen
                drawRect(
                    color = Color(0x99000000),
                    size = size
                )

                // 3) cut out the transparent window
                drawRect(
                    color = Color.Transparent,
                    topLeft = Offset(left, top),
                    size = Size(boxW, boxH),
                    blendMode = BlendMode.Clear
                )

                // 4) optional red scan line
                if (showLine) {
                    drawLine(
                        color = Color.Red,
                        start = Offset(left, centerY),
                        end = Offset(right, centerY),
                        strokeWidth = 3.dp.toPx(),
                        blendMode = BlendMode.SrcOver
                    )
                }
            }
    )
}
