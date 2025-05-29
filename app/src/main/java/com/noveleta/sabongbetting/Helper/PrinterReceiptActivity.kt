package com.noveleta.sabongbetting.Helper

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import androidx.activity.viewModels

import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.collectAsState

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.platform.LocalContext

import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.SharedPreference.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.*

class PrinterReceiptActivity : ComponentActivity() {

    override fun onStart() {
        super.onStart()
        SunmiPrinterHelper.initSunmiPrinterService(this) {
            // Printer is ready to use
        }
    }

    override fun onStop() {
        super.onStop()
        SunmiPrinterHelper.deInitSunmiPrinterService(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyComposeApplicationTheme {
            val context = LocalContext.current
            val activity = (context as PrinterReceiptActivity)
            
                PrinterScreen(
                    onBackPressed = { activity.finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val activity = (context as PrinterReceiptActivity)
    
    val viewModel: BettingViewModel = viewModel()
    val viewModelCallWebsocket: CallWebsocketDashboard = viewModel()
    val viewModelStaffBetData: PlaceBetsViewModel = viewModel()
    val viewModelPrintMoneyOnHandReports: SendMoneyOnHandViewModel = viewModel()
    val viewModelCashInData: SendCashInTellerViewModel = viewModel()
    val viewModelCashOutData: SendCashOutTellerViewModel = viewModel()
    val viewModelReprintBet: ReprintBetViewModel = viewModel()
    val viewModelPayoutData: SendPayoutViewModel = viewModel()
    val viewModelMobileDepositData: SendMobileDepositViewModel = viewModel()
    val viewModelMobileWithdrawData: SendMobileWithdrawViewModel = viewModel()
    val viewModelCancelBetData: SendCancelBetViewModel = viewModel()
    
    val betCancelResponse by viewModelCancelBetData.betResponse.collectAsState()
    val betCancelResult by viewModelCancelBetData.betResult.collectAsState()
    val betCancelErrorCode by viewModelCancelBetData.betErrorCode.collectAsState()
    
    val mobileDepositResponse by viewModelMobileDepositData.betResponse.collectAsState()
    val mobileDepositResult   by viewModelMobileDepositData.betResult.collectAsState()
    val mobileDepositErrorCode   by viewModelMobileDepositData.betErrorCode.collectAsState()
    
    val mobileWithdrawResponse by viewModelMobileWithdrawData.betResponse.collectAsState()
    val mobileWithdrawResult   by viewModelMobileWithdrawData.betResult.collectAsState()
    val mobileWithdrawErrorCode   by viewModelMobileWithdrawData.betErrorCode.collectAsState()
    
    val betPayoutResponse by viewModelPayoutData.betResponse.collectAsState()
    
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
            
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thermal Printer", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFFFFFFF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF19181B)
                ),
                modifier = Modifier.height(56.dp)
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.Black)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
            
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Ready to Print", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    
                    viewModelCallWebsocket.sendDashboardTrigger()
                    viewModelCallWebsocket.sendBetsTrigger()
                    if(betResponse != null){
                    BetReceiptDialog(
                        response = betResponse!!,
                        onDismiss = {
                            viewModel.clearBetState()
                            activity.finish()
                            }
                    )
                    
                    LaunchedEffect(betResponse) {
                            // only runs a single time per distinct betResponse
                            printBetResponse(context, betResponse!!)
                    }
                    
                    }else if (printMOHResponse != null && printMOHResults == 0){
                    MoneyOnHandDialog(
        response = printMOHResponse!!,
        onPrint = {
        printMoneyOnHand(context, printMOHResponse!!)
        },
        onDismiss = {
            viewModelPrintMoneyOnHandReports.clearBetState()
            activity.finish()
        }
    )

                    }else if (reprintResponse != null && reprintErrorCode == 0) {
                    ReprintBetReceiptDialog(
        response = reprintResponse!!,
        onDismiss = {
            viewModelReprintBet.clearBetState()
            activity.finish()
        }
    )
LaunchedEffect(reprintResponse) {
        rePrintBetResponse(context, reprintResponse!!)
        }
                    }else if (cashOutResponse != null) {
                    TellerFundCashOutReceiptDialog(
        response = cashOutResponse!!,
        onDismiss = {
            viewModelCashOutData.clearBetState()
            activity.finish()
        }
    )
    LaunchedEffect(cashOutResponse) {
        printTellerCashoutResponse(context, cashOutResponse!!)
        }
                    }else if (cashInResponse != null) {
TellerFundCashInReceiptDialog(
        response = cashInResponse!!,
        onDismiss = {
            viewModelCashInData.clearBetState()
            activity.finish()
        }
    )
LaunchedEffect(cashInResponse) {
        printTellerCashinResponse(context, cashInResponse!!)
    }
    
    } else if (betPayoutResponse != null){
    PayoutReceiptDialog(betPayoutResponse!!){
            viewModelPayoutData.clearBetState()
            activity.finish()
        }
    LaunchedEffect(betPayoutResponse) {
             printPayout(context, betPayoutResponse!!)
             }
    }else if (mobileDepositResponse != null){
      MobileDepositReceiptDialog(mobileDepositResponse!!){
        viewModelMobileDepositData.clearBetState()
        activity.finish()
      }
      
      LaunchedEffect(mobileDepositResponse) {
        printMobileDeposit(context, mobileDepositResponse!!)
    }
    }else if (mobileWithdrawResponse != null){
      MobileWithdrawReceiptDialog(mobileWithdrawResponse!!){
        viewModelMobileWithdrawData.clearBetState()
        activity.finish()
      }
      
      LaunchedEffect(mobileWithdrawResponse) {
        printMobileWithdraw(context, mobileWithdrawResponse!!)
    }
    }else if(betCancelResponse != null && betCancelErrorCode == 0){
            CancelReceiptDialog(betCancelResponse!!){
            viewModelCancelBetData.clearBetState()
            activity.finish()
            }
            
            LaunchedEffect(betCancelResponse) {
        printCancelledBetting(context, betCancelResponse!!)
        }
    } // End of else if
    
    
    
                    
                }
            }
        }
    )
}
