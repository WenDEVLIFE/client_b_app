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
            .background(Color(0xFF19181B))
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
    val columns = listOf("No", "Transaction", "Amount", "Reprint", "Date")
    val isDarkTheme = isSystemInDarkTheme()
    val iconTint = Color(0xFFFFFFFF)

    var currentPage by remember { mutableStateOf(0) }
    var showAll by remember { mutableStateOf(false) }
    val rowsPerPage = 5
    val pageCount = (fightHistory.size + rowsPerPage - 1) / rowsPerPage
    val paginatedList = if (showAll) fightHistory else fightHistory.drop(currentPage * rowsPerPage).take(rowsPerPage)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF313131), shape = RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Column {
                // Header
                
                Row(modifier = Modifier.height(50.dp)) {
    val transaction = entry.transaction ?: "-"
    val amount = entry.amount ?: "0"
    val date = entry.eventDate ?: "-"
    val transactionCode = entry.transactionCode

    // No. # 1 2...
    Box(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (showAll) (index + 1).toString() else (currentPage * rowsPerPage + index + 1).toString(),
            color = Color(0xFFFFFFFF),
            fontSize = 13.sp
        )
    }

    // Transaction
    Box(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = transaction,
            color = Color(0xFFFFFFFF),
            fontSize = 13.sp
        )
    }

    // Amount
    Box(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = amount,
            color = Color(0xFFFFFFFF),
            fontSize = 13.sp
        )
    }

    // Reprint Icon Column (2nd to last)
    Box(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight()
            .clickable(enabled = !transactionCode.isNullOrBlank()) {
                transactionCode?.let { onReprintClick(it) }
            },
        contentAlignment = Alignment.Center
    ) {
        if (!transactionCode.isNullOrBlank()) {
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

    // Date
    Box(
        modifier = Modifier
            .width(80.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            color = Color(0xFFFFFFFF),
            fontSize = 13.sp
        )
    }
}

                // Data rows
                paginatedList.forEachIndexed { index, entry ->
                    runCatching {
                        Row(modifier = Modifier.height(50.dp)) {
                            val transaction = entry.transaction ?: "-"
                            val amount = entry.amount ?: "0"
                            val date = entry.eventDate ?: "-"

                            listOf(
                                if (showAll) (index + 1).toString() else (currentPage * rowsPerPage + index + 1).toString(),
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
                                        color = Color(0xFFFFFFFF),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Pagination Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Prev + Page numbers + Next
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!showAll) {
                  if (currentPage > 0){
                     TextButton(
                        onClick = { if (currentPage > 0) currentPage-- },
                        enabled = currentPage > 0
                    ) {
                        Text("< Prev",
                        color = Color(0xFFFFFFFF),
                        fontSize = 12.sp
                        )
                    }
                  }
 

                    for (i in 0 until pageCount) {
                        TextButton(onClick = { currentPage = i }) {
                            Text(
                                text = "${i + 1}",
                                color = if (i == currentPage) Color(0xFFFFFFFF) else Color.Gray,
                                fontSize = 12.sp,
                                modifier = if (i == currentPage) {
                                    Modifier
                                        .background(
                                            color = Color(0xFF4A90E2),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                } else Modifier
                            )
                        }
                    }
                    
                    if (currentPage < pageCount - 1){
                       TextButton(
                        onClick = { if (currentPage < pageCount - 1) currentPage++ },
                        enabled = currentPage < pageCount - 1
                    ) {
                        Text("Next >",
                        color = Color(0xFFFFFFFF),
                        fontSize = 12.sp)
                    }
                    }
                    
                }
            }

            // Right: Total pages + Show All toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!showAll) {
                    Text(
                        text = "$pageCount",
                        color = Color(0xFFFFFFFF),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                TextButton(onClick = {
                    showAll = !showAll
                    currentPage = 0
                }) {
                    Text(
                        text = if (showAll) "Hide All" else "Show All",
                        fontSize = 12.sp,
                        color = Color(0xFFFFFFFF)
                    )
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
    val columns = listOf("No", "Date", "Teller", "Fight #", "Bettor", "Bet Under", "Ammount", "Status", "Is Returned?")
    val isDarkTheme = isSystemInDarkTheme()
    val iconTint = Color(0xFFFFFFFF)

    var currentPage by remember { mutableStateOf(0) }
    var showAll by remember { mutableStateOf(false) }
    val rowsPerPage = 5
    val pageCount = (fightHistory.size + rowsPerPage - 1) / rowsPerPage
    val paginatedList = if (showAll) fightHistory else fightHistory.drop(currentPage * rowsPerPage).take(rowsPerPage)

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
                                color = Color(0xFFFFFFFF),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Data Rows
                paginatedList.forEachIndexed { index, entry ->
                    runCatching {
                        Row(modifier = Modifier.height(50.dp)) {
                            val cells = listOf(
                                if (showAll) (index + 1).toString() else (currentPage * rowsPerPage + index + 1).toString(),
                                entry.date,
                                entry.teller,
                                entry.fightNumber,
                                entry.bettor,
                                entry.betUnder,
                                entry.amount,
                                entry.status,
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
                                        color = Color(0xFFFFFFFF),
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }.onFailure { error ->
                        Log.e("currentBetTableUI", "Error rendering row $index: ${error.message}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pagination Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Prev + Page numbers + Next
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!showAll) {
                    if (currentPage > 0) {
                        TextButton(onClick = { currentPage-- }) {
                            Text("< Prev",
                            fontSize = 12.sp,
                        color = Color(0xFFFFFFFF))
                        }
                    }

                    for (i in 0 until pageCount) {
                        TextButton(onClick = { currentPage = i }) {
                            Text(
                                text = "${i + 1}",
                                fontSize = 12.sp,
                                color = if (i == currentPage) Color(0xFFFFFFFF) else Color.Gray,
                                modifier = if (i == currentPage) {
                                    Modifier
                                        .background(Color(0xFF4A90E2), shape = RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                } else Modifier
                            )
                        }
                    }

                    if (currentPage < pageCount - 1) {
                        TextButton(onClick = { currentPage++ }) {
                            Text("Next >",
                            fontSize = 12.sp,
                        color = Color(0xFFFFFFFF))
                        }
                    }
                }
            }

            // Right: Total Pages + Show All
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!showAll) {
                    Text(
                        text = "$pageCount",
                        color = Color(0xFFFFFFFF),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                TextButton(onClick = {
                    showAll = !showAll
                    currentPage = 0
                }) {
                    Text(
                        text = if (showAll) "Hide All" else "Show All",
                        color = Color(0xFFFFFFFF),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


}
