package com.noveleta.sabongbetting.Dashboard.Content;

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

import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.*

@Composable
fun liveBetting(data: LiveBettingData) {


val fightHistoryGridData: List<Pair<Int, String>> = data?.fights?.mapNotNull { entry ->
    val number = entry.number.toIntOrNull()
    val result = when (entry.status) {
        "Meron Won" -> "MERON"
        "Wala Won" -> "WALA"
        "Draw" -> "DRAW"
        "Cancelled" -> "CANCELLED"
        else -> null
    }

    if (number != null && result != null) {
        number to result
    } else null
}?.takeIf { it.isNotEmpty() } ?: listOf(
    75 to "MERON",
    40 to "DRAW",
    35 to "WALA",
    33 to "MERON",
    24 to "DRAW",
    86 to "WALA",
    20 to "MERON",
    58 to "DRAW",
    47 to "WALA"
)

   val currentFight = data?.fightNumber?.toIntOrNull() ?: 0


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        
     if (data == null) {
        Text("Connecting to server...")
     } else {
      
        // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF19181B)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "New Noveleta Cockpit Arena",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Two columns side‑by‑side, spaced by 20.dp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ← LEFT BOX
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // Label
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(Color(0xFF9A2121)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "MERON",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Total Payout
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(Color(0xFFBB3C3C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = data.meronTotalBetAmount ?: "",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Hits / Your Payout
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .background(Color(0xFFF14747)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = data.bannerName ?: "",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "PAYOUT: " + data.meronText ?: "",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // ← RIGHT BOX
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // Label
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(Color(0xFF1640AA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "WALA",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Total Payout
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(Color(0xFF174CC9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = data.walaTotalBetAmount ?: "",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Management Payouts
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .background(Color(0xFF1B56E2)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = data.promoterName ?: "",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "PAYOUT: " + data.walaText ?: "",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            // Fight Status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(Color(0xFF323134)),
                contentAlignment = Alignment.Center
            ) {
            
            Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "FIGHT",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                if(data.isBetting == "1"){
                 //Current Fight Num
                
                Text(
                    text = data.fightNumber ?: "",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                //Fight Status and Result
                Text(
                    text = "STATUS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Open",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2EB132)
                )
                }else if(data.isBetting == "4"){
                 //Current Fight Num
                
                Text(
                    text = data.fightNumber ?: "",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                //Fight Status and Result
                Text(
                    text = "STATUS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "LAST CALL",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB12D36)
                )
                }else if(data.isBetting == "5"){
                 //Current Fight Num
                
                Text(
                    text = data.fightNumber ?: "",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                //Fight Status and Result
                Text(
                    text = "STATUS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cancelled",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                }else if(data.isBetting == "7"){
                Text(
                    text = "Pending",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                }else if(data.isBetting == "3" || data.isBetting == "6"){
                
                /*
                //
                // TRIGGER TIMER AS SOON RESULT AND BET CLOSSED
                //
                */
                if(data.isBettingWinner == "1"){
                 //Current Fight Num
                
                Text(
                    text = data.fightNumber ?: "",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                //Fight Status and Result
                Text(
                    text = "STATUS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RESULT\nMERON",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                }else if(data.isBettingWinner == "2"){
                 //Current Fight Num
                
                Text(
                    text = data.fightNumber ?: "",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                //Fight Status and Result
                Text(
                    text = "STATUS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RESULT\nWALA",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                }else{
                 //Current Fight Num
                
                Text(
                    text = data.fightNumber ?: "",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                //Fight Status and Result
                Text(
                    text = "STATUS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RESULT\nDRAW",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                }
                
                }else{
                Text(
                    text = "CLOSED",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB12D36)
                )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                }
            }

          tableLayout.FightHistoryOneGrid(fightHistory = fightHistoryGridData, currentFight = currentFight)
           
        }
    }
    
    
    
    }
}
