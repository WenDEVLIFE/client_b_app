package com.noveleta.sabongbetting.widgets;

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close


object DigitInputBox {

    @Composable
fun DigitInputBoxDisplay(clickableMeron: (Int) -> Unit, clickableDraw: (Int) -> Unit, clickableWala: (Int) -> Unit) {
    var digitDisplay by remember { mutableStateOf(0) }
    val numberCounts = remember { mutableStateMapOf<Int, Int>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Digit display and clear button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = digitDisplay.toString(),
                style = TextStyle(fontSize = 32.sp),
                modifier = Modifier.weight(1f),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            IconButton(onClick = {
                digitDisplay = 0
                numberCounts.clear()
            }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
            }
        }

        // Number list: 100, 200, ..., 1000, 2000, 3000
        val numbers = (1..10).map { it * 100 } + listOf(2000, 3000)

        // Manual Grid layout using Column + Rows
        val rows = numbers.chunked(3)
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { num ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(4.dp)
                            .width(90.dp)
                            .height(50.dp)
                            .clickable {
                                val count = numberCounts.getOrDefault(num, 0) + 1
                                numberCounts[num] = count
                                digitDisplay += num
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = num.toString(),
                                color = Color.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bet Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            
           BetButton("Bet Meron", Color(0xFFB12D36)) { clickableMeron(digitDisplay) }
           BetButton("Bet Draw", Color(0xFF2EB132)) { clickableDraw(digitDisplay) }
           BetButton("Bet Wala", Color(0xFF2070E1)) { clickableWala(digitDisplay) }

        }
        
    }
}

    @Composable
    fun BetButton(text: String, color: Color, clickableBet: () -> Unit) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 90.dp, height = 50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color)
                .clickable { clickableBet() }
        ) {
            Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
    
    @Composable
    fun TellerButton(text: String, color: Color, clickableBet: () -> Unit) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 150.dp, height = 50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color)
                .clickable { clickableBet() }
        ) {
            Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
    
    @Composable
    fun BetClaimPayout(text: String, color: Color, clickableBet: () -> Unit) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(color)
                .clickable { clickableBet() }
        ) {
            Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
