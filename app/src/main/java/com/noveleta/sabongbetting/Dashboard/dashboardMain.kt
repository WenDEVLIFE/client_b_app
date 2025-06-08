package com.noveleta.sabongbetting.ui

// App-specific imports
import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.Network.*
import com.noveleta.sabongbetting.Dashboard.Content.*
import com.noveleta.sabongbetting.*
import com.noveleta.sabongbetting.Enter.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.ui.Modifier.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.MainActivity
import com.noveleta.sabongbetting.SharedPreference.*

// Android & Compose imports
import android.app.Activity


import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.content.ContextCompat
import androidx.compose.material3.AlertDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.Image
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.material3.ModalNavigationDrawer
import kotlinx.coroutines.delay
import android.util.Log
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.ColorFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWithDrawer() {
    // Create ViewModels
    val viewModelDashboardData: LiveBettingViewModel = viewModel()
    val viewModelStaffBetData: PlaceBetsViewModel = viewModel()
    val dashboardData by viewModelDashboardData.dashboardData.observeAsState()
    val staffLiveBetData by viewModelStaffBetData.dashboardData.observeAsState()
    
   
    LaunchedEffect(dashboardData) {
        Log.d("Composable", "Dashboard data changed: $dashboardData")
    }
    
   
    LaunchedEffect(Unit) {
        Log.d("Composable", "Connecting WebSockets")
        viewModelDashboardData.connectWebSocket()
        //viewModelStaffBetData.connectWebSocket()
    }
    
   
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (dashboardData != null) "Data received!" else "Waiting for data...",
            style = MaterialTheme.typography.headlineMedium
        )
        dashboardData?.let { data ->
            Text("Data received:", style = MaterialTheme.typography.bodyLarge)
           
            Text("Field 1: ${data.fightNumber}")
            Text("Field 1: ${data.promoterName}")
        }
    }
}
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWithDrawer(networkMonitor: NetworkMonitor) {
    
    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    val viewModelDashboardData: LiveBettingViewModel = viewModel()
    val viewModelStaffBetData: PlaceBetsViewModel = viewModel()
    val viewModelPayoutData: SendPayoutViewModel = viewModel()
    val viewModelCallWebsocket: CallWebsocketDashboard = viewModel()
    
    val errorMsg by viewModelDashboardData.errorMessage.collectAsState()
    val clipboard = LocalClipboardManager.current
    
    val betResponse by viewModelPayoutData.betResponse.collectAsState()
    val betResult by viewModelPayoutData.betResult.collectAsState()
    val betErrorCode   by viewModelPayoutData.betErrorCode.collectAsState()
    
    val dashboardData by viewModelDashboardData.liveBettingData.observeAsState()
    val staffLiveBetData by viewModelStaffBetData.dashboardData.observeAsState(initial = null)
    val historyTransactionLogs by viewModelStaffBetData.transactionHistoryList.observeAsState()
    
    // State for exit confirmation dialog
    var showExitDialog by remember { mutableStateOf(false) }
    // State for connection progress dialog
    var showConnectingDialog by remember { mutableStateOf(true) }
    var showScannerDialog by remember { mutableStateOf(false) }
    var scanFinish by remember { mutableStateOf(false) }
    
    val userRole = SessionManager.roleID ?: "2"
    val companyId = SessionManager.accountID ?: "500"
    val navController = rememberNavController()


    val transactionCode by viewModelPayoutData.transactionCode.collectAsState()
var showScanner by remember { mutableStateOf(false) }

// Scanner launcher with modern result API
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val code = intent?.getStringExtra("scanned_code") ?: ""

            if (code.isNotEmpty()) {
                try {
                    viewModelPayoutData.setTransactionCode(code)
            viewModelPayoutData.claimPayout(
            context,
                userID = companyId,
                roleID = userRole,
                barcodeResult = code
            )
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Invalid barcode format", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
   
    val isConnected by networkMonitor.isConnected.collectAsState()

    var signalLevel by remember { mutableStateOf(0) }

    // Periodically update signal strength
    LaunchedEffect(isConnected) {
        if (isConnected) {
            while (true) {
                signalLevel = networkMonitor.getSignalLevel()
                delay(3000) // Check every 3 seconds
            }
        }
    }

    val iconRes = when {
        !isConnected -> R.drawable.ic_wifi_low
        signalLevel >= 3 -> R.drawable.ic_wifi_high
        signalLevel == 2 -> R.drawable.ic_wifi_mid
        signalLevel == 1 -> R.drawable.ic_wifi_low
        else -> R.drawable.ic_wifi_off
    }

    
    LaunchedEffect(Unit) {
        // Connect websockets and hide connecting dialog when done
        viewModelDashboardData.connectWebSocket()
        viewModelStaffBetData.connectWebSocket()
        // Simplified: hide after a delay or based on a flag in ViewModel
        delay(2000L)
        showConnectingDialog = false
    }
    
    errorMsg?.let { message ->
        AlertDialog(
            onDismissRequest = {
                viewModelDashboardData.closeWebSocket()
                viewModelStaffBetData.closeWebSocket()
                activity?.finishAffinity()
            },
            title = { Text("WebSocket Error") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    viewModelDashboardData.connectWebSocket()
        viewModelStaffBetData.connectWebSocket()
                }) {
                    Text("Reconnect")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    activity?.finishAffinity()
                }) {
                    Text("Quit")
                }
            }
        )
    }

    
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = listOf(DrawerScreen.liveBet, DrawerScreen.placeBet, DrawerScreen.claimPayout, DrawerScreen.cancelBet,
     DrawerScreen.currentBetsLog, DrawerScreen.withdrawDepositTicket, 
     DrawerScreen.transactionLog)
     
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val isDarkTheme = isSystemInDarkTheme()
    
     
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .width(400.dp)
                    .background(Color(0xFF171F2D))
            ) {
                // Top section: profile details
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 24.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text(
        text = "Dashboard",
        style = MaterialTheme.typography.titleMedium,
        color = Color.White
    )
    Text(
        text = "Log Out",
        modifier = Modifier.clickable{
        showExitDialog = true
        },
        style = MaterialTheme.typography.titleMedium,
        color = Color.Red
    )
}

                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.height(12.dp))

                    val cname = SessionManager.cname ?: "N/A"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                    ) {
                        Column {
                            Text(
                                text = cname,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Teller Staff",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Divider(color = Color.Gray)

                    Spacer(Modifier.height(8.dp))

                    // Navigation drawer items
                    items.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        val background = if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(background)
                                .clickable {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(screen.route) { launchSingleTop = true }
                                    }
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = screen.iconRes),
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp),
                                tint = if (isSelected) Color.White else Color.Gray
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
    topBar = {
        Row(
    modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF19181B))
        .height(56.dp)
        .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    IconButton(onClick = { scope.launch { drawerState.open() } }) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Open drawer",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }

    Spacer(modifier = Modifier.width(16.dp))

    Text(
        text = "Sabong Betting",
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        modifier = Modifier.weight(1f) // takes available space
    )

    // Icons grouped at end
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "WiFi Signal Strength",
            modifier = Modifier.size(24.dp),
            tint = if (isConnected) Color.Green else Color.Red
        )

        Spacer(modifier = Modifier.width(12.dp))

        IconButton(onClick = {
            showScannerDialog = true
        }) {
            Image(
                painter = painterResource(id = R.drawable.ic_scan_barcode),
                contentDescription = "Scan Barcode",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

    }
) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = DrawerScreen.placeBet.route,
                modifier = Modifier.padding(innerPadding)
            ) {
            
    composable(DrawerScreen.liveBet.route) {
    if (dashboardData != null) {
        liveBetting(dashboardData!!)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

    composable(DrawerScreen.placeBet.route) {
    val staffData = staffLiveBetData
    val logs = historyTransactionLogs
   // testLayout()
    if (staffData != null && dashboardData != null) {
       staffBet(staffData, dashboardData!!, viewModelDashboardData, viewModelStaffBetData)
    } else {
        // Loading state or placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
    
}

composable(DrawerScreen.claimPayout.route) {
    if (dashboardData != null) {
        payoutUI(viewModelDashboardData, viewModelStaffBetData)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

composable(DrawerScreen.cancelBet.route) {
if (dashboardData != null) {
        cancelBetUI(viewModelDashboardData, viewModelStaffBetData)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

composable(DrawerScreen.currentBetsLog.route) {
if (dashboardData != null) {
        currentBetListUI(dashboardData!!)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

composable(DrawerScreen.withdrawDepositTicket.route) {
if (dashboardData != null) {
        withdrawDepositUI()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

composable(DrawerScreen.transactionLog.route) {
if (dashboardData != null) {
        transactionLogsUI(dashboardData!!)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}
            }
        }
        
         // Connecting to server progress dialog
    if (showConnectingDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Connecting to server...") },
            text = { Text("Please wait while the app establishes connection.") },
            confirmButton = {}
        )
    }
    
    if (showScannerDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)), // Dim the background
            contentAlignment = Alignment.Center
        ) {
        
         if(scanFinish){
    viewModelPayoutData.claimPayout(
      context,
        userID = companyId,
        roleID = userRole,
        barcodeResult = transactionCode
      )
    }
    
            // 1. The scanner is always visible when the overlay is active
            BarcodeScannerScreen(
                onScanResult = { code ->
                    if (code.isNotEmpty()) {
                       viewModelPayoutData.setTransactionCode(code)
              scanFinish = true
                    }
                },
                onCancel = {
                   showScannerDialog = false
                }
            )
        if (betResponse != null) {
                PayoutReceiptDialog(betResponse!!) {
                    viewModelPayoutData.clearBetState()
                }
                LaunchedEffect(betResponse) {
                viewModelCallWebsocket.sendDashboardTrigger()
    viewModelCallWebsocket.sendBetsTrigger()
    viewModelDashboardData.connectWebSocket()
    viewModelStaffBetData.refreshWebSocket()
                    if (!SessionManager.isSunmiDevice) {
        printWebsocketPOS.sendPayoutPrint(
            ip = SessionManager.posIpAddress ?: "192.168.8.100",
            port = SessionManager.posPortAddress ?: "8080",
            payoutResponse = betResponse!!,
            SessionManager.cname ?: "",
            SessionManager.userpassword ?: ""
        )
                            }else{
                            printPayout(context, betResponse!!)
                            }
                }
            } else if (betErrorCode == -1) {
                PrintBetPayoutErrorResults(betResult) {
                    viewModelPayoutData.clearBetState()
                    showScannerDialog = false
                }
            }
        }
    }

        // Exit confirmation dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Exit App") },
                text = { Text("Are you sure you want to exit the app?") },
                confirmButton = {
                    TextButton(onClick = {
                        activity?.finish()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
        
        
        
    }
}
