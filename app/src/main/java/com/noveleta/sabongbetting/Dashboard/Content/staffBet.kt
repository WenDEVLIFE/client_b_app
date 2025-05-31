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

import androidx.compose.foundation.horizontalScroll

import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import androidx.compose.ui.platform.LocalDensity

import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.SharedPreference.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun staffBet(staffBetData: PlaceBetsData, liveBetData: LiveBettingData, viewModelDashboardData: LiveBettingViewModel, viewModelStaffBetData: PlaceBetsViewModel) { 

    val cashHandlers = liveBetData.cashHandlerNames ?: emptyList()
    
    val transactionLogs = liveBetData.userTransactionLogs ?: emptyList()
    
    val currentFight = liveBetData?.fightNumber?.toIntOrNull() ?: 75
    
    var showCashInTellerDialog by remember { mutableStateOf(false) }
    var showCashOutTellerDialog by remember { mutableStateOf(false) }
    var showMeronClosed by remember { mutableStateOf(false) }
    var showEmptyAmount by remember { mutableStateOf(false) }
    var showWalaClosed by remember { mutableStateOf(false) }
    var showDrawClosed by remember { mutableStateOf(false) }
    var showWarningBetDraw by remember { mutableStateOf(false) }
    var cashInOrOut by remember { mutableStateOf("Cash In") } 
    
    val viewModel: BettingViewModel = viewModel()
    
    
    val viewModelCallWebsocket: CallWebsocketDashboard = viewModel()
    
    val viewModelPrintMoneyOnHandReports: SendMoneyOnHandViewModel = viewModel()
    val viewModelCashInData: SendCashInTellerViewModel = viewModel()
    val viewModelCashOutData: SendCashOutTellerViewModel = viewModel()
    val viewModelReprintBet: ReprintBetViewModel = viewModel()
    
    val reprintResponse by viewModelReprintBet.betResponse.collectAsState()
    val reprintErrorCode by viewModelReprintBet.betErrorCode.collectAsState()
    val reprintResult by viewModelReprintBet.betResult.collectAsState()
    
    val printMOHResponse by viewModelPrintMoneyOnHandReports.betResponse.collectAsState()
    val printMOHErrorCode by viewModelPrintMoneyOnHandReports.betErrorCode.collectAsState()
    val printMOHResults by viewModelPrintMoneyOnHandReports.betResult.collectAsState()
    
    val cashInResponse by viewModelCashInData.betResponse.collectAsState()
    val cashInErrorCode by viewModelCashInData.betErrorCode.collectAsState()
    val cashInResult by viewModelCashInData.betResult.collectAsState()
    
    val cashOutResponse by viewModelCashOutData.betResponse.collectAsState()
    val cashOutErrorCode by viewModelCashOutData.betErrorCode.collectAsState()
    val cashOutResult by viewModelCashOutData.betResult.collectAsState()
    
    val betResponse by viewModel.betResponse.collectAsState()
    val betResult by viewModel.betResult.collectAsState()
    val betErrorCode by viewModel.betErrorCode.collectAsState()
    val betMessage by viewModel.betMessage.collectAsState()
    
    val isLoading by viewModel.isLoading.collectAsState()
    
    val context = LocalContext.current
    
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    
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
    val digitDisplayState = remember { mutableStateOf("0") }
    val density = LocalDensity.current

    if (betResponse != null) {

    //--------------------- 
    //
    // TODO:: Call Live Dashboard on PC Side after Betting
    //
    //---------------------
                    
    BetReceiptDialog(
                        response = betResponse!!,
                        onDismiss = {
                            viewModel.clearBetState()
                            }
                    )
                    
                    LaunchedEffect(betResponse) {
                            // only runs a single time per distinct betResponse
                            viewModelCallWebsocket.sendDashboardTrigger()
                            viewModelCallWebsocket.sendBetsTrigger()
                            viewModelDashboardData.connectWebSocket()
                            viewModelStaffBetData.refreshWebSocket()
                            printBetResponse(context, betResponse!!)
                            printWebsocketPOS.sendBetResponsePrint(
            ip = SessionManager.posIpAddress ?: "192.168.8.100",
            port = SessionManager.posPortAddress ?: "8080",
            payoutResponse = betResponse!!,
            SessionManager.cname ?: "",
            SessionManager.userpassword ?: ""
        )
        
                    }
    
    digitDisplayState.value = "0"
    

}else if (betResponse == null && betErrorCode == -1) {

    PrintBetErrorResults(betResult, betMessage){
    viewModel.clearBetState()
    }

}

if(printMOHResponse != null && printMOHResults == 0){
viewModelCallWebsocket.sendDashboardTrigger()
    viewModelCallWebsocket.sendBetsTrigger()
    viewModelCallWebsocket.sendAndroidBetsTrigger()
    viewModelCallWebsocket.sendAndroidDashboardTrigger()
MoneyOnHandDialog(
        response = printMOHResponse!!,
        onPrint = {
        printMoneyOnHand(context, printMOHResponse!!)
        printWebsocketPOS.sendMoneyOnHandReport(
            ip = SessionManager.posIpAddress ?: "192.168.8.100",
            port = SessionManager.posPortAddress ?: "8080",
            payoutResponse = printMOHResponse!!,
            SessionManager.cname ?: "",
            SessionManager.userpassword ?: ""
        )
        },
        onDismiss = {
            viewModelPrintMoneyOnHandReports.clearBetState()
        }
    )

    
}else if(printMOHResponse == null && printMOHErrorCode == -1){
PrintMOHError(printMOHResults){
    viewModelPrintMoneyOnHandReports.clearBetState()
    }
}

if (reprintResponse != null && reprintErrorCode == 0) {
    viewModelCallWebsocket.sendDashboardTrigger()
    viewModelCallWebsocket.sendBetsTrigger()
    viewModelCallWebsocket.sendAndroidBetsTrigger()
    viewModelCallWebsocket.sendAndroidDashboardTrigger()
ReprintBetReceiptDialog(
        response = reprintResponse!!,
        onDismiss = {
            viewModelReprintBet.clearBetState()
        }
    )
LaunchedEffect(reprintResponse) {
        rePrintBetResponse(context, reprintResponse!!)
        printWebsocketPOS.sendReprintBet(
            ip = SessionManager.posIpAddress ?: "192.168.8.100",
            port = SessionManager.posPortAddress ?: "8080",
            payoutResponse = reprintResponse!!,
            SessionManager.cname ?: "",
            SessionManager.userpassword ?: ""
        )
        }
    
}else if (reprintResponse == null && reprintErrorCode == -1) {

    RePrintBetErrorResults(reprintResult){
    viewModelReprintBet.clearBetState()
    }

}

if (cashOutResponse != null) {
viewModelCallWebsocket.sendDashboardTrigger()
    viewModelCallWebsocket.sendBetsTrigger()
    viewModelCallWebsocket.sendAndroidBetsTrigger()
    viewModelCallWebsocket.sendAndroidDashboardTrigger()
TellerFundCashOutReceiptDialog(
        response = cashOutResponse!!,
        onDismiss = {
            viewModelCashOutData.clearBetState()
        }
    )
    LaunchedEffect(cashOutResponse) {
        printTellerCashoutResponse(context, cashOutResponse!!)
        printWebsocketPOS.sendCashOutTeller(
            ip = SessionManager.posIpAddress ?: "192.168.8.100",
            port = SessionManager.posPortAddress ?: "8080",
            payoutResponse = reprintResponse!!,
            SessionManager.cname ?: "",
            SessionManager.userpassword ?: ""
        )
        }

}else if (cashOutErrorCode == -1) {
    PrintTellerCashOutErrorResults(cashOutResult){
    viewModelCashOutData.clearBetState()
    }
}

if (cashInResponse != null) {
viewModelCallWebsocket.sendDashboardTrigger()
    viewModelCallWebsocket.sendBetsTrigger()
    viewModelCallWebsocket.sendAndroidBetsTrigger()
    viewModelCallWebsocket.sendAndroidDashboardTrigger()
TellerFundCashInReceiptDialog(
        response = cashInResponse!!,
        onDismiss = {
            viewModelCashInData.clearBetState()
        }
    )
LaunchedEffect(cashInResponse) {
        printTellerCashinResponse(context, cashInResponse!!)
        printWebsocketPOS.sendCashInTeller(
            ip = SessionManager.posIpAddress ?: "192.168.8.100",
            port = SessionManager.posPortAddress ?: "8080",
            payoutResponse = reprintResponse!!,
            SessionManager.cname ?: "",
            SessionManager.userpassword ?: ""
        )
    }
}else if (cashInErrorCode == -1) {

    PrintTellerCashInErrorResults(cashInResult){
    viewModelCashInData.clearBetState()
    }

}

    val cardWidth = 260.dp
    val spacing = 8.dp
    val totalCardWidth = cardWidth + spacing
    val currentIndex = remember { mutableStateOf(0) }

    val totalCardWidthPx = with(density) { totalCardWidth.toPx() }
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val containerWidth = cardWidth  // So only one card is shown centered

    fun scrollToCard(index: Int) {
        val centeredOffset = (totalCardWidthPx * index - (screenWidthPx - totalCardWidthPx) / 45).toInt()
        coroutineScope.launch {
            scrollState.animateScrollTo(
                centeredOffset.coerceIn(0, scrollState.maxValue),
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
        }
    }

    val items = listOf(
        BetItemCards("Meron", liveBetData?.meronText ?: "", liveBetData?.meronTotalBetAmount ?: "", Color(0xFFB12D36)),
        BetItemCards("Draw", liveBetData?.drawText ?: "", liveBetData?.drawTotalBetAmount ?: "", Color(0xFF2EB132)),
        BetItemCards("Wala", liveBetData?.walaText ?: "", liveBetData?.walaTotalBetAmount ?: "", Color(0xFF2070E1))
    )
    
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF19181B))) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
    Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxWidth()
) {
    // LEFT arrow (always occupies space)
    Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center) {
        if (currentIndex.value > 0) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Scroll Left",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        currentIndex.value--
                        scrollToCard(currentIndex.value)
                    },
                tint = Color.White
            )
        }
    }

    // Scrollable Row
    Box(
        modifier = Modifier
            .width(containerWidth)
            .horizontalScroll(scrollState)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
            items.forEach { item ->
                BetInfoCards.InfoCard(
                    title = item.title,
                    payout = item.payout,
                    totalBets = item.totalBets,
                    backgroundColor = item.backgroundColor,
                    modifier = Modifier
                        .width(cardWidth)
                        .padding(vertical = 8.dp)
                )
            }
        }
    }

    // RIGHT arrow (always occupies space)
    Box(modifier = Modifier.size(33.dp), contentAlignment = Alignment.Center) {
        if (currentIndex.value < items.lastIndex) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Scroll Right",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        currentIndex.value++
                        scrollToCard(currentIndex.value)
                    },
                tint = Color.White
            )
        }
    }
}

    
    
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
            DigitInputBox.TellerButton(
                "Teller CashIn",
                Color(0xFFB12D36),
                modifier = Modifier.weight(1f)
            ) {
                cashInOrOut = "Cash In"
                showCashInTellerDialog = true
            }

            DigitInputBox.TellerButton(
                "Teller CashOut",
                Color(0xFFB12D36),
                modifier = Modifier.weight(1f)
            ) {
                cashInOrOut = "Cash Out"
                showCashOutTellerDialog = true
            }

            DigitInputBox.TellerButton(
                "Money On Hand",
                Color(0xFFB12D36),
                modifier = Modifier.weight(1f)
            ) {
                viewModelPrintMoneyOnHandReports.sendMoneyOnHandReport(
                    context,
                    userID = companyId,
                    roleID = userRole
                )
            }
        }

             Spacer(modifier = Modifier.height(8.dp))
             
             if (liveBetData?.isBetting == 1 ?: "" || liveBetData?.isBetting == 4 ?: ""){
             Spacer(modifier = Modifier.height(16.dp))
             
             Text(
                    text = "No Fight Started, Come Back Later.",
                    fontSize = 20.sp,
                    color = Color.White
                )
             }else{
             DigitInputBox.DigitInputBoxDisplay(
             digitDisplayState = digitDisplayState,
             clickableMeron = { betAmount ->
             
             if(betAmount == 0){
               showEmptyAmount = true
             }else{
               viewModel.placeBet(context,userID = companyId, roleID = userRole, drawTotalBet = staffBetData?.drawTotalBetAmount ?: "", betType = 1, betAmount = betAmount)
             }
             
             },
             
             clickableDraw = { betAmount ->
           
             if(betAmount == 0){
               showEmptyAmount = true
             }else{
               viewModel.placeBet(context,userID = companyId, roleID = userRole, drawTotalBet = staffBetData?.drawTotalBetAmount ?: "", betType = 3, betAmount = betAmount)
             }
             
             },
             clickableWala = { betAmount ->
             
             if(betAmount == 0){
               showEmptyAmount = true
             }else{
               viewModel.placeBet(context,userID = companyId, roleID = userRole, drawTotalBet = staffBetData?.drawTotalBetAmount ?: "", betType = 2, betAmount = betAmount)
             }
             
             })
             
             Spacer(modifier = Modifier.height(16.dp))
             }// End of Else for Checking isBetting
             
             Spacer(modifier = Modifier.height(8.dp))
             
             Divider()
             
             Spacer(modifier = Modifier.height(8.dp))
             
             Text(
                    text = "Current Bet List",
                    fontSize = 20.sp,
                    color = Color.White
                )
                
             Spacer(modifier = Modifier.height(16.dp))   
              
             tableLayout.logHistoryTable(
                fightHistory = liveBetData.userTransactionLogs ?: emptyList(),
                onReprintClick = { transactionCode ->
                   Log.d("Reprint", "Clicked reprint for: $transactionCode")
                   viewModelReprintBet.reprintBet(context,companyId,userRole,transactionCode)
                }
             )
             
        }//End Of Column with default Padding start 8 and end 8
    }//End of Box Wrap inside column content
    
    
    /* DIALOGS */
    
    // Cash In Dialog
if (showCashInTellerDialog) {
    CashTellerDialog(
        cashHandlers = liveBetData.cashHandlerNames ?: emptyList(),
        cashInOrOut = cashInOrOut,
        onDismiss = { showCashInTellerDialog = false },
        onConfirm = { cashAmount, selectedHandlerId, password ->
            // handle the result (cashAmount, selectedHandlerId, password, cashInOrOut)
            viewModelCashInData.sendCashInTeller(
            context,
        userID = companyId,
         roleID = userRole, 
         cashAmount,
         selectedHandlerId,
         password
        )
            showCashInTellerDialog = false
        }
    )
}

if(showWalaClosed){

WalaClosed(){
showWalaClosed = false
}

}

if(showMeronClosed){

MeronClosed(){
showMeronClosed = false
}

}

if(showDrawClosed){

DrawClosed(){
showDrawClosed = false
}

}

if (showEmptyAmount){
NoValueEntered(){
showEmptyAmount = false
}
}


if (showCashOutTellerDialog) {
    CashTellerDialog(
        cashHandlers = liveBetData.cashHandlerNames ?: emptyList(),
        cashInOrOut = cashInOrOut,
        onDismiss = { showCashOutTellerDialog = false },
        onConfirm = { cashAmount, selectedHandlerId, password ->
            // handle the result (cashAmount, selectedHandlerId, password, cashInOrOut)
            viewModelCashOutData.sendCashOutTeller(
            context,
        userID = companyId,
         roleID = userRole, 
         cashAmount,
         selectedHandlerId,
         password
        )
            showCashOutTellerDialog = false
        }
    )
}

/*
if (showWarningBetDraw) {
    AlertDialog(
        onDismissRequest = { showWarningBetDraw = false },
        confirmButton = {
            TextButton(onClick = { showWarningBetDraw = false }) {
                Text("Okay")
            }
        },
        title = {
            Text("Bet Limit Exceeded")
        },
        text = {
            Text("Your bet exceeds the allowed draw limit. Please enter a smaller amount.")
        }
    )
}*/

}
