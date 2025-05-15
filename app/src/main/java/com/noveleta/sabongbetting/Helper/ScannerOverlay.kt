package com.noveleta.sabongbetting.Helper



import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope  // only if you need its type directly
import androidx.compose.ui.draw.drawWithContent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity


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