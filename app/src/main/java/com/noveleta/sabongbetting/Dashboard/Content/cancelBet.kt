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
fun cancelBetUI() {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    
    val viewModelCancelBetData: SendCancelBetViewModel = viewModel()
    val betResponse by viewModelCancelBetData.betResponse.collectAsState()
    val betResult by viewModelCancelBetData.betResult.collectAsState()
    val betErrorCode by viewModelCancelBetData.betErrorCode.collectAsState()
    
    // dialog state
    var showDialog by remember { mutableStateOf(false) }
    
    val userRole = SessionManager.roleID ?: "2"
    val companyId = SessionManager.accountID ?: "500"
    
    if(betResponse != null && betErrorCode == 0){
            CancelReceiptDialog(betResponse!!){
            viewModelCancelBetData.clearBetState()
            }
            
            LaunchedEffect(betResponse) {
        delay(3000) // 3 seconds
        printCancelledBetting(context, betResponse!!)
    }
            }else if (betErrorCode == -1) {
            PrintCancelBetErrorResults(betResult){
            viewModelCancelBetData.clearBetState()
            }
            }
    
    val transactionCode by viewModelCancelBetData.transactionCode.collectAsState()
var showScanner by remember { mutableStateOf(false) }

// Scanner launcher with modern result API
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val code = intent?.getStringExtra("scanned_code") ?: ""

            if (code.isNotEmpty()) {
                    viewModelCancelBetData.setTransactionCode(code)
            viewModelCancelBetData.sendCancelBetBarcode(
                context,
                userID = companyId,
                roleID = userRole,
                barcodeTxt = code
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
                    "Cancel Ongoing Bet: Scan Receipt",
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
                            val intent = Intent(context, ScannerActivity::class.java)
            scannerLauncher.launch(intent)
                        }
                )
            }

            Spacer(Modifier.height(16.dp))

            DigitInputBox.BetClaimPayout("Manual Input", Color(0xFF2EB132)) {
                showDialog = true
            }
        }

        // --- DIALOG ---
        if (showDialog) {
            AlertDialog(onDismissRequest = { showDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
    value = transactionCode,
    onValueChange = { 
        if (it.length <= 14 && it.all { char -> char.isDigit() }) {
            viewModelCancelBetData.setTransactionCode(it)
        }
    },
    placeholder = { Text("Enter Transaction Code") },
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number
    )
)


                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { /* handle payout */ 
                            viewModelCancelBetData.sendCancelBetBarcode(context,userID = companyId, roleID = userRole, barcodeTxt = transactionCode)
                            showDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Confirm Cancel Bet")
                        }

                        Spacer(Modifier.height(8.dp))

                        IconButton(onClick = { showDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }
            }
        }
    }
}
