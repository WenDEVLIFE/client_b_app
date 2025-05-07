package com.noveleta.sabongbetting.Dashboard.Content

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

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
fun withdrawDepositUI() {
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
    
    if (mobileDepositResponse != null){
      MobileDepositReceiptDialog(mobileDepositResponse!!){
        viewModelMobileDepositData.clearBetState()
      }
      
      LaunchedEffect(mobileDepositResponse) {
        delay(3000) // 3 seconds
        printMobileDeposit(context, mobileDepositResponse!!)
    }
    } else if (mobileDepositResponse == null && mobileDepositErrorCode == -1) {
      PrintDepositErrorResults(mobileDepositResult){
        viewModelMobileDepositData.clearBetState()
      }
    }
    
    if (mobileWithdrawResponse != null){
      MobileWithdrawReceiptDialog(mobileWithdrawResponse!!){
        viewModelMobileWithdrawData.clearBetState()
      }
      LaunchedEffect(mobileWithdrawResponse) {
        delay(3000) // 3 seconds
        printMobileWithdraw(context, mobileWithdrawResponse!!)
    }
    } else if (mobileWithdrawResponse == null && mobileWithdrawErrorCode == -1) {
      PrintWithdrawErrorResults(mobileWithdrawResult){
        viewModelMobileWithdrawData.clearBetState()
      }
    }
    
    val userRole = SessionManager.roleID ?: "2"
    val companyId = SessionManager.accountID ?: "500"
            
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
                
                TextField(
                  value = widthdrawCode,
                  onValueChange = { 
                  if (it.length <= 14 && it.all { char -> char.isDigit() }) {
                      widthdrawCode = it
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
                    onClick = { /* handle payout */ 
                    viewModelMobileWithdrawData.sendMobileWithdraw(userID = companyId, roleID = userRole, barcodeResult = widthdrawCode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(20.dp)
                  ) {
                    Text("Claim Withdraw")
                    }
                    
                    Text(
                    "Mobile Deposit Points",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))
                
                TextField(
                  value = depositCode,
                  onValueChange = { 
                  if (it.length <= 14 && it.all { char -> char.isDigit() }) {
                      depositCode = it
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
                    onClick = { /* handle payout */ 
                    viewModelMobileDepositData.sendMobileDeposit(userID = companyId, roleID = userRole, barcodeResult = depositCode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(20.dp)
                  ) {
                    Text("Send Deposit")
                    }
                
                }
           }   
     }            
}