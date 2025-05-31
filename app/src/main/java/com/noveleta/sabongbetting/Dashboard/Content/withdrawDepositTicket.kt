package com.noveleta.sabongbetting.Dashboard.Content

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

import coil.compose.AsyncImage

import com.google.accompanist.systemuicontroller.rememberSystemUiController

import kotlinx.coroutines.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

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
fun withdrawDepositUI() {
val activity = LocalContext.current as Activity
    val context = LocalContext.current

    var widthdrawCode by remember { mutableStateOf("") }
    var depositCode by remember { mutableStateOf("") }

    val viewModelMobileDepositData: SendMobileDepositViewModel = viewModel()
    val mobileDepositResponse by viewModelMobileDepositData.betResponse.collectAsState()
    val mobileDepositResult   by viewModelMobileDepositData.betResult.collectAsState()
    val mobileDepositErrorCode   by viewModelMobileDepositData.betErrorCode.collectAsState()
    
    val viewModelMobileWithdrawData: SendMobileWithdrawViewModel = viewModel()
    val mobileWithdrawResponse by viewModelMobileWithdrawData.betResponse.collectAsState()
    val mobileWithdrawResult   by viewModelMobileWithdrawData.betResult.collectAsState()
    val mobileWithdrawErrorCode   by viewModelMobileWithdrawData.betErrorCode.collectAsState()
    
    
    val userRole = SessionManager.roleID ?: "2"
    val companyId = SessionManager.accountID ?: "500"
            
    val isDarkTheme = isSystemInDarkTheme()
    val viewModelCallWebsocket: CallWebsocketDashboard = viewModel()
    val viewModelPayoutData: SendPayoutViewModel = viewModel()
    val betResponse by viewModelPayoutData.betResponse.collectAsState()
    val betResult   by viewModelPayoutData.betResult.collectAsState()
    val betErrorCode   by viewModelPayoutData.betErrorCode.collectAsState()
    
    // dialog state
    var showDialog by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var scanFinish by remember { mutableStateOf(false) }
    var showScannerDialog by remember { mutableStateOf(false) }
    var showScannerDialogMobileWithdraw by remember { mutableStateOf(false) }
    var showScannerDialogMobileDeposit by remember { mutableStateOf(false) }
    
    var scanFinishWithdraw by remember { mutableStateOf(false) }
    var scanFinishDeposit by remember { mutableStateOf(false) }
    
    if(betResponse != null){
    viewModelCallWebsocket.sendDashboardTrigger()
    viewModelCallWebsocket.sendBetsTrigger()
    viewModelCallWebsocket.sendAndroidBetsTrigger()
    viewModelCallWebsocket.sendAndroidDashboardTrigger()
        PayoutReceiptDialog(betResponse!!){
            viewModelPayoutData.clearBetState()
        }
        viewModelPayoutData.setTransactionCode("")
        scanFinish = false
    LaunchedEffect(betResponse) {
             printPayout(context, betResponse!!)
             }
    }else if (betErrorCode == -1) {
    
      PrintBetPayoutErrorResults(betResult){
            viewModelPayoutData.clearBetState()
        }
        viewModelPayoutData.setTransactionCode("")
        scanFinish = false
    }
    
    val transactionCodeWithdraw by viewModelMobileWithdrawData.transactionCodeWithdraw.collectAsState()
    val transactionDepositCode by viewModelMobileDepositData.transactionDepositCode.collectAsState()
    
    if (mobileDepositResponse != null){
      
      viewModelMobileDepositData.setTransactionCode("")
      scanFinishDeposit = false
      MobileDepositReceiptDialog(mobileDepositResponse!!){
        viewModelMobileDepositData.clearBetState()
        activity.finish()
      }
      
      LaunchedEffect(mobileDepositResponse) {
        printMobileDeposit(context, mobileDepositResponse!!)
        printWebsocketPOS.sendMobileDepositPrint(
            ip = SessionManager.posIpAddress ?: "192.168.8.100",
            port = SessionManager.posPortAddress ?: "8080",
            payoutResponse = mobileDepositResponse!!,
            SessionManager.cname ?: "",
            SessionManager.userpassword ?: ""
        )
    }
    } else if (mobileDepositErrorCode == -1) {
      PrintDepositErrorResults(mobileDepositResult){
        viewModelMobileDepositData.clearBetState()
      }
      viewModelMobileDepositData.setTransactionCode("")
      scanFinishDeposit = false
    }
    
    if (mobileWithdrawResponse != null){
      
      viewModelMobileWithdrawData.setTransactionCode("")
      scanFinishWithdraw = false
      MobileWithdrawReceiptDialog(mobileWithdrawResponse!!){
        viewModelMobileWithdrawData.clearBetState()
        activity.finish()
      }
      
      LaunchedEffect(mobileWithdrawResponse) {
        printMobileWithdraw(context, mobileWithdrawResponse!!)
        printWebsocketPOS.sendMobileWithdrawPrint(
            ip = SessionManager.posIpAddress ?: "192.168.8.100",
            port = SessionManager.posPortAddress ?: "8080",
            payoutResponse = mobileWithdrawResponse!!,
            SessionManager.cname ?: "",
            SessionManager.userpassword ?: ""
        )
    }
    
    } else if (mobileWithdrawErrorCode == -1) {
      PrintWithdrawErrorResults(mobileWithdrawResult){
        viewModelMobileWithdrawData.clearBetState()
      }
      viewModelMobileWithdrawData.setTransactionCode("")
      scanFinishWithdraw = false
    }
    
    if(scanFinishDeposit){
    viewModelMobileDepositData.sendMobileDeposit(context, userID = companyId, roleID = userRole, barcodeResult = transactionDepositCode)
    }
    
    if(scanFinishWithdraw){
    viewModelMobileWithdrawData.sendMobileWithdraw(context, userID = companyId, roleID = userRole, barcodeResult = transactionCodeWithdraw)
    }
    
    // Scanner launcher with modern result API
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val code = intent?.getStringExtra("scanned_code") ?: ""

            if (code.isNotEmpty()) {
                    viewModelPayoutData.setTransactionCode(code)
      viewModelPayoutData.claimPayout(
      context,
        userID = companyId,
        roleID = userRole,
        barcodeResult = code
      )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF19181B))) {
    
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Mobile Withdraw Points",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_scan_barcode),
                    contentDescription = "Scan Barcode",
                    colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            showScannerDialogMobileWithdraw = true
                         }
                )
            }

            Spacer(Modifier.height(16.dp))

            TextField(
                  value = transactionCodeWithdraw,
                  onValueChange = { 
                  if (it.length <= 31 && it.all { char -> char.isDigit() }) {
                      viewModelMobileWithdrawData.setTransactionCode(it)
                     }
                  },
                  placeholder = { Text("Enter Barcode") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  keyboardOptions = KeyboardOptions(
                     keyboardType = KeyboardType.Number
                    )
                  )
            Spacer(Modifier.height(16.dp))

            Button(
                    onClick = { /* handle withdraw */ 
                    viewModelMobileWithdrawData.sendMobileWithdraw(context, userID = companyId, roleID = userRole, barcodeResult = widthdrawCode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(20.dp)
                  ) {
                    Text("Claim Withdraw")
                    }
                    
            Spacer(Modifier.height(16.dp))      
                  
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Mobile Deposit Points",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_scan_barcode),
                    contentDescription = "Scan Barcode",
                    colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            showScannerDialogMobileDeposit = true
                         }
                )
            }   
            
            Spacer(Modifier.height(16.dp))
            
            TextField(
                  value = transactionDepositCode,
                  onValueChange = { 
                  if (it.length <= 31 && it.all { char -> char.isDigit() }) {
                      viewModelMobileDepositData.setTransactionCode(it)
                     }
                  },
                  placeholder = { Text("Enter Barcode") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  keyboardOptions = KeyboardOptions(
                     keyboardType = KeyboardType.Number
                    )
                  )
            Spacer(Modifier.height(16.dp))

            Button(
                    onClick = { /* handle deposit */ 
                    viewModelMobileDepositData.sendMobileDeposit(context, userID = companyId, roleID = userRole, barcodeResult = depositCode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(20.dp)
                  ) {
                    Text("Send Deposit")
                    }
        }

        // --- DIALOG ---
        if(showScannerDialogMobileDeposit){
        BarcodeScannerScreen(
            onScanResult = { code ->
              viewModelMobileDepositData.setTransactionCode(code)
              scanFinishDeposit = true
      showScannerDialogMobileDeposit = false
            },
            onCancel = {
              showScannerDialogMobileDeposit = false
            }
          )
        }
        
        if(showScannerDialogMobileWithdraw){
        BarcodeScannerScreen(
            onScanResult = { code ->
              viewModelMobileWithdrawData.setTransactionCode(code)
              scanFinishWithdraw = true
      showScannerDialogMobileWithdraw = false
            },
            onCancel = {
              showScannerDialogMobileWithdraw = false
            }
          )
        }
    }
}