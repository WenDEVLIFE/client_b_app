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
import androidx.compose.runtime.livedata.observeAsState
import android.widget.Toast

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.ColorFilter


import coil.compose.AsyncImage

import com.google.accompanist.systemuicontroller.rememberSystemUiController

import kotlinx.coroutines.*


import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.SharedPreference.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.*

@Composable
fun staffBet(staffBetData: PlaceBetsData, liveBetData: LiveBettingData) { 

    val cashHandlers = liveBetData.cashHandlerNames ?: emptyList()
    
    val transactionLogs = liveBetData.userTransactionLogs ?: emptyList()
    
    val currentFight = liveBetData?.fightNumber?.toIntOrNull() ?: 75
    
    var showCashTellerDialog by remember { mutableStateOf(false) }
    var cashInOrOut by remember { mutableStateOf("Cash In") } 
    
    val viewModel: BettingViewModel = viewModel()
    val viewModelStaffBetData: PlaceBetsViewModel = viewModel()
    val viewModelCashInData: SendCashInTellerViewModel = viewModel()
    val viewModelCashOutData: SendCashOutTellerViewModel = viewModel()
    val viewModelReprintBet: ReprintBetViewModel = viewModel()
    
    val reprintResponse by viewModelReprintBet.betResponse.collectAsState()
    val reprintResult by viewModelReprintBet.betResult.collectAsState()
    
    val cashInResponse by viewModelCashInData.betResponse.collectAsState()
    val cashInResult by viewModelCashInData.betResult.collectAsState()
    
    val cashOutResponse by viewModelCashOutData.betResponse.collectAsState()
    val cashOutResult by viewModelCashOutData.betResult.collectAsState()
    
    val betResponse by viewModel.betResponse.collectAsState()
    val betResult by viewModel.betResult.collectAsState()
    
    val isLoading by viewModel.isLoading.collectAsState()
    
    val context = LocalContext.current
    
    val isDarkTheme = isSystemInDarkTheme()
    val iconTint = if (isDarkTheme) Color.White else Color.Black
    
    val datalive = remember { mutableStateOf(liveBetData) }
    val datalivetwo = remember { mutableStateOf(staffBetData) }
    
    // This recompose the model to update for any changes
    LaunchedEffect(liveBetData){
       datalive.value = liveBetData
    }
    
    LaunchedEffect(staffBetData){
       datalivetwo.value = staffBetData
    }

    val userRole = SessionManager.roleID ?: "2"
    val companyId = SessionManager.accountID ?: "500"
    
if (betResponse != null) {
    BetReceiptDialog(
        response = betResponse!!,
        onDismiss = {
            viewModel.clearBetState()
        }
    )

    LaunchedEffect(betResponse) {
        delay(3000) // 3 seconds
        printBetResponse(context, betResponse!!)
    }
}else if (betResponse == null && betResult == -1) {
    
    PrintBetErrorResults(betResult){
    viewModel.clearBetState()
    }
    
}

if (reprintResponse != null) {
    ReprintBetReceiptDialog(
        response = reprintResponse!!,
        onDismiss = {
            viewModelReprintBet.clearBetState()
        }
    )

    LaunchedEffect(reprintResponse) {
        delay(3000) // 3 seconds
        rePrintBetResponse(context, reprintResponse!!)
    }
}else if (reprintResponse == null) {
    
    RePrintBetErrorResults(reprintResult){
    viewModelReprintBet.clearBetState()
    }
    
}

if (cashOutResponse != null) {
TellerFundCashOutReceiptDialog(
        response = cashOutResponse!!,
        onDismiss = {
            viewModelCashOutData.clearBetState()
        }
    )
LaunchedEffect(cashOutResponse) {
        delay(3000) // 3 seconds
        printTellerCashoutResponse(context, cashOutResponse!!)
    }
}else if (cashOutResponse == null) {
    PrintTellerCashOutErrorResults(cashOutResult){
    viewModelCashOutData.clearBetState()
    }
}

if (cashInResponse != null) {
TellerFundCashInReceiptDialog(
        response = cashInResponse!!,
        onDismiss = {
            viewModel.clearBetState()
        }
    )
LaunchedEffect(cashInResponse) {
        delay(3000) // 3 seconds
        printTellerCashinResponse(context, cashInResponse!!)
    }
}else if (cashInResponse == null) {

    PrintTellerCashOutErrorResults(cashInResult){
    viewModelCashInData.clearBetState()
    }
    
}


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
    BetInfoCards.InfoCard(
        title = "Meron",
        payout = liveBetData?.meronText ?: "0",
        totalBets = liveBetData?.meronTotalBetAmount ?: "0",
        backgroundColor = Color(0xFFB12D36),
        modifier = Modifier.width(300.dp)
    )
    BetInfoCards.InfoCard(
        title = "Draw",
        payout = liveBetData?.drawText ?: "0",
        totalBets = liveBetData?.drawTotalBetAmount ?: "0",
        backgroundColor = Color(0xFF2EB132),
        modifier = Modifier.width(300.dp)
    )
    BetInfoCards.InfoCard(
        title = "Wala",
        payout = liveBetData?.walaText ?: "0",
        totalBets = liveBetData?.walaTotalBetAmount ?: "0",
        backgroundColor = Color(0xFF2070E1),
        modifier = Modifier.width(300.dp)
    )
             }
             Spacer(modifier = Modifier.height(8.dp))
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    DigitInputBox.TellerButton("Teller CashIn", Color(0xFFB12D36)) {
        cashInOrOut = "Cash In"
        showCashTellerDialog = true
    }
    DigitInputBox.TellerButton("Teller CashOut", Color(0xFFB12D36)) {
        cashInOrOut = "Cash Out"
        showCashTellerDialog = true
    }
}

             if (liveBetData?.fightStatus == "CLOSED" ?: ""){
             Spacer(modifier = Modifier.height(16.dp))
             Text(
                    text = "No Fight",
                    fontSize = 20.sp,
                    color = Color.White
                )
             }else{
             
             DigitInputBox.DigitInputBoxDisplay(clickableMeron = { betAmount ->
             viewModel.placeBet(userID = companyId, roleID = userRole, betType = 1, betAmount = betAmount)
             },
             clickableDraw = { betAmount ->
             viewModel.placeBet(userID = companyId, roleID = userRole, betType = 3, betAmount = betAmount)
             },
             clickableWala = { betAmount ->
             viewModel.placeBet(userID = companyId, roleID = userRole, betType = 2, betAmount = betAmount)
             })
             
             Spacer(modifier = Modifier.height(16.dp))
             }
             Spacer(modifier = Modifier.height(8.dp))
             Divider()
             Spacer(modifier = Modifier.height(8.dp))
             Text(
                    text = "Transaction Logs History",
                    fontSize = 20.sp,
                    color = Color.White
                )
             Spacer(modifier = Modifier.height(16.dp))   
              
       tableLayout.logHistoryTable(
            fightHistory = liveBetData.userTransactionLogs ?: emptyList(),
            onReprintClick = { transactionCode ->
               Log.d("Reprint", "Clicked reprint for: $transactionCode")
               viewModelReprintBet.reprintBet(companyId,userRole,transactionCode.toInt())
            }
        )
             
        }
    }
    
    // Show dialog if needed
if (showCashTellerDialog) {
    CashTellerDialog(
        cashHandlers = liveBetData.cashHandlerNames ?: emptyList(),
        cashInOrOut = cashInOrOut,
        onDismiss = { showCashTellerDialog = false },
        onConfirm = { cashAmount, selectedHandlerId, password ->
            // handle the result (cashAmount, selectedHandlerId, password, cashInOrOut)
            
            showCashTellerDialog = false
        }
    )
}

}
