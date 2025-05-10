package com.noveleta.sabongbetting.Helper;

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

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

import java.util.concurrent.Executors
import androidx.compose.ui.graphics.Color

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

    // Overlay: Cancel button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = {
                // shut down scanner to free resources
                scanner.close()
                cameraExecutor.shutdown()
                onCancel()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel scanning",
                tint = Color.White
            )
        }
    }
}
