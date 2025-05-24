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



import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp


object DigitInputBox {

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitInputBoxDisplay(
    digitDisplayState: MutableState<String>,
    clickableMeron: (Int) -> Unit,
    clickableDraw: (Int) -> Unit,
    clickableWala: (Int) -> Unit
) {
    val digitDisplay by digitDisplayState
    
    val numberCounts = remember { mutableStateMapOf<Int, Int>() }

    // To show numeric keyboard and allow user input
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Fixed width for buttons and cards for alignment
    val buttonWidth = 100.dp
    val buttonHeight = 40.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Digit display with editable TextField
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = digitDisplayState.value,
                onValueChange = { newValue ->
                    // Allow only digits, no leading zeros unless single zero
                    if (newValue.all { it.isDigit() }) {
                        digitDisplayState.value = newValue.trimStart('0').ifEmpty { "0" }
                        if (digitDisplayState.value.isEmpty()) digitDisplayState.value = "0"
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .clickable {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    },
                textStyle = LocalTextStyle.current.copy(fontSize = 32.sp, textAlign = TextAlign.Center, color = Color(0xFFFFFFFF)),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color(0xFFFFFFFF),
                    containerColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFFFFFFF),
                    unfocusedBorderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            IconButton(onClick = {
                digitDisplayState.value = "0"
                numberCounts.clear()
                keyboardController?.hide()
            }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFFFFFFFF))
            }
        }

        // Number list cards
        val numbers = (1..10).map { it * 100 } + listOf(2000, 3000)
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
                            .width(buttonWidth)
                            .height(buttonHeight)
                            .clickable {
                                val currentDigit = digitDisplayState.value.toIntOrNull() ?: 0
                                val count = numberCounts.getOrDefault(num, 0) + 1
                                numberCounts[num] = count
                                digitDisplayState.value = (currentDigit + num).toString()
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
            BetButton("Bet Meron", Color(0xFFB12D36), buttonWidth, 40.dp) {
                clickableMeron(digitDisplayState.value.toIntOrNull() ?: 0)
            }
            BetButton("Bet Draw", Color(0xFF2EB132), buttonWidth, 40.dp) {
                clickableDraw(digitDisplayState.value.toIntOrNull() ?: 0)
            }
            BetButton("Bet Wala", Color(0xFF2070E1), buttonWidth, 40.dp) {
                clickableWala(digitDisplayState.value.toIntOrNull() ?: 0)
            }
        }
    }
}

@Composable
fun BetButton(text: String, color: Color, width: Dp, height: Dp, clickableBet: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable { clickableBet() }
    ) {
        Text(text = text, color = Color(0xFFFFFFFF), fontWeight = FontWeight.Bold)
    }
}

    
    @Composable
    fun TellerButton(text: String, color: Color, clickableBet: () -> Unit) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 150.dp, height = 40.dp)
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
