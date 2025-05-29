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

import androidx.compose.runtime.livedata.observeAsState

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
fun transactionLogsUI(liveBetData: LiveBettingData) {
    
    val userRole = SessionManager.roleID ?: "2"
    val companyId = SessionManager.accountID ?: "500"
    
    val context = LocalContext.current
    
    val viewModelReprintBet: ReprintBetViewModel = viewModel()
    
    val reprintResponse by viewModelReprintBet.betResponse.collectAsState()
    val reprintErrorCode by viewModelReprintBet.betErrorCode.collectAsState()
    val reprintResult by viewModelReprintBet.betResult.collectAsState()
    
    if (reprintResponse != null && reprintErrorCode == 0) {
    val intent = Intent(context, PrinterReceiptActivity::class.java)
    startActivity(intent)
}else if (reprintErrorCode == -1) {
    
    RePrintBetErrorResults(reprintResult){
    viewModelReprintBet.clearBetState()
    }
    
}

    
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF19181B))) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text(
                    text = "Current Bet List",
                    fontSize = 20.sp,
                    color = Color.White
                )
                
            Spacer(modifier = Modifier.height(16.dp))   
              
            tableLayout.logHistoryTable(
            fightHistory = liveBetData.userTransactionLogs ?: emptyList(),
            onReprintClick = { transactionCode ->
                   viewModelReprintBet.reprintBet(context,companyId,userRole,transactionCode)
                }
            )
             
        }
    }
    
}