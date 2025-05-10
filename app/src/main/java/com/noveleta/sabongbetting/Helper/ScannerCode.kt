package com.noveleta.sabongbetting.Helper

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

@OptIn(ExperimentalPermissionsApi::class)
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
    // PreviewView remembered to avoid re-creation
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    // CameraX setup and teardown
    DisposableEffect(key1 = previewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        var cameraProvider: ProcessCameraProvider? = null

        val listener = Runnable {
            cameraProvider = cameraProviderFuture.get()

            // Preview use-case
            val previewUseCase = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // ImageAnalysis use-case
            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { useCase ->
                    useCase.setAnalyzer(cameraExecutor) { imageProxy ->
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
                                            ?.let { code ->
                                                // Stop further analysis
                                                cameraProvider?.unbindAll()
                                                onScanResult(code)
                                            }
                                    }
                                    .addOnFailureListener {
                                        // Log or handle error
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } ?: imageProxy.close()
                    }
                }

            // Bind use-cases to lifecycle
            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    previewUseCase,
                    analysisUseCase
                )
            } catch (exc: Exception) {
                // Handle binding errors
            }
        }
        cameraProviderFuture.addListener(listener, ContextCompat.getMainExecutor(context))

        onDispose {
            // Cleanup resources
            scanner.close()
            cameraExecutor.shutdown()
            cameraProvider?.unbindAll()
        }
    }

    // UI: Camera preview
    AndroidView(
        factory = { previewView },
        modifier = modifier
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
                // shutdown and cancel
                scanner.close()
                cameraExecutor.shutdown()
                onCancel()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel scanning",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}
