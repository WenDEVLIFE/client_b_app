package com.noveleta.sabongbetting.widgets;

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel

import android.widget.Toast

import coil.compose.AsyncImage

import com.google.accompanist.systemuicontroller.rememberSystemUiController

import kotlinx.coroutines.*

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.ColorFilter

import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.*

object tableLayout {

@Composable
fun FightHistoryTwoGrid(
    fightHistory: List<Pair<Int, String>>,
    currentFight: Int
) {
    val cellBorder = BorderStroke(1.dp, Color.DarkGray)

    // Group data
    val meronList = fightHistory.filter { it.second == "MERON" }.map { it.first }
    val drawList = fightHistory.filter { it.second == "DRAW" }.map { it.first }
    val walaList = fightHistory.filter { it.second == "WALA" }.map { it.first }

    // Find the longest list
    val maxRows = listOf(meronList.size, drawList.size, walaList.size).maxOrNull() ?: 0

    val columns = listOf(
        "MERON" to Color(0xFFB12D36),
        "DRAW"  to Color(0xFF2EB132),
        "WALA"  to Color(0xFF2070E1)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF313131), shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            columns.forEach { (label, color) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(cellBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }

        // Grid rows
        for (i in 0 until maxRows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                listOf(meronList, drawList, walaList).forEachIndexed { index, resultList ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(cellBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        val number = resultList.getOrNull(i)
                        if (number != null) {
                            Text(
                                text = number.toString(),
                                color = if (number == currentFight) columns[index].second else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FightHistoryOneGrid(
    fightHistory: List<Pair<Int, String>>,
    currentFight: Int
) {
    val cellBorder = BorderStroke(1.dp, Color.DarkGray)

    // Group data
    val meronList = fightHistory.filter { it.second == "MERON" }.map { it.first }
    val drawList = fightHistory.filter { it.second == "DRAW" }.map { it.first }
    val walaList = fightHistory.filter { it.second == "WALA" }.map { it.first }

    // Find the longest list
    val maxRows = listOf(meronList.size, drawList.size, walaList.size).maxOrNull() ?: 0

    val columns = listOf(
        "MERON" to Color(0xFFB12D36),
        "DRAW"  to Color(0xFF2EB132),
        "WALA"  to Color(0xFF2070E1)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            columns.forEach { (label, color) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(cellBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }

        // Grid rows
        for (i in 0 until maxRows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                listOf(meronList, drawList, walaList).forEachIndexed { index, resultList ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(cellBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        val number = resultList.getOrNull(i)
                        if (number != null) {
                            Text(
                                text = number.toString(),
                                color = if (number == currentFight) columns[index].second else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun logHistoryTable(
    fightHistory: List<FightLogEntry>,
    onReprintClick: (String) -> Unit
) {
    val columns = listOf("No", "Transaction", "Amount", "Date", "Action")
    val isDarkTheme = isSystemInDarkTheme()
    val iconTint = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF313131), shape = RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Column {
                    // Header
                    Row(modifier = Modifier.height(40.dp)) {
                        columns.forEach { label ->
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Data rows
                    fightHistory.forEachIndexed { index, entry ->
                        runCatching {
                            Row(modifier = Modifier.height(50.dp)) {
                                val transaction = entry.transaction ?: "-"
                                val amount = entry.amount ?: "0"
                                val date = entry.eventDate ?: "-"

                                listOf(
                                    (index + 1).toString(),
                                    transaction,
                                    amount,
                                    date
                                ).forEach { cell ->
                                    Box(
                                        modifier = Modifier
                                            .width(80.dp)
                                            .fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cell,
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                // QR Reprint Button
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .fillMaxHeight()
                                        .clickable(enabled = !entry.transactionCode.isNullOrBlank()) {
                                            entry.transactionCode?.let { onReprintClick(it) }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_open),
                                        contentDescription = "Reprint",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(2.dp),
                                        colorFilter = ColorFilter.tint(iconTint)
                                    )
                                }
                            }
                        }.onFailure { error ->
                            Log.e("logHistoryTable", "Error rendering row $index: ${error.message}")
                        }
                    }
                }
            }
    }
}

@Composable
fun currentBetTableUI(
    fightHistory: List<CurrentBetLogs>,
    onReprintClick: (String) -> Unit
) {
    val columns = listOf("No", "Date", "Teller", "Fight #", "Bettor", "Bet Under", "Ammount", "Status", "Result", "Is Claimed?", "Is Returned?")
    val isDarkTheme = isSystemInDarkTheme()
    val iconTint = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF313131), shape = RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Column {
                // Header
                Row(modifier = Modifier.height(40.dp)) {
                    columns.forEach { label ->
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Data Rows
                fightHistory.forEachIndexed { index, entry ->
                    runCatching {
                        Row(modifier = Modifier.height(50.dp)) {
                            val cells = listOf(
                                (index + 1).toString(),
                                entry.date,
                                entry.teller,
                                entry.fightNumber,
                                entry.bettor,
                                entry.betUnder,
                                entry.amount,
                                entry.status,
                                entry.result,
                                entry.isClaimed,
                                entry.isReturned
                            )

                            cells.forEach { cell ->
                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cell,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Optional QR Reprint button (if needed, uncomment below)
                            /*
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .fillMaxHeight()
                                    .clickable(enabled = entry.transactionCode != null) {
                                        entry.transactionCode?.let { onReprintClick(it) }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_open),
                                    contentDescription = "Reprint",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(2.dp),
                                    colorFilter = ColorFilter.tint(iconTint)
                                )
                            }
                            */
                        }
                    }.onFailure { error ->
                        Log.e("logHistoryTable", "Error rendering row $index: ${error.message}")
                    }
                }
            }
        }
    }
}


}
