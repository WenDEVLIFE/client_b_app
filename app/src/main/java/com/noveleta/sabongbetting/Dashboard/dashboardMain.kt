package com.noveleta.sabongbetting.ui

// App-specific imports
import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.Network.*
import com.noveleta.sabongbetting.Dashboard.Content.*
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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.ColorFilter

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
fun MainWithDrawer() {
    // Obtain activity for finish() and launching MainActivity
    val activity = LocalContext.current as? Activity
    val context = LocalContext.current
    val viewModelDashboardData: LiveBettingViewModel = viewModel()
    val viewModelStaffBetData: PlaceBetsViewModel = viewModel()
    val viewModelPayoutData: SendPayoutViewModel = viewModel()
    
    val betResponse by viewModelPayoutData.betResponse.collectAsState()
    val betResult by viewModelPayoutData.betResult.collectAsState()
    
    val dashboardData by viewModelDashboardData.dashboardData.observeAsState()
    val staffLiveBetData by viewModelStaffBetData.dashboardData.observeAsState(initial = null)
    val historyTransactionLogs by viewModelStaffBetData.transactionHistoryList.observeAsState()
    
    // State for exit confirmation dialog
    var showExitDialog by remember { mutableStateOf(false) }
    // State for connection progress dialog
    var showConnectingDialog by remember { mutableStateOf(true) }
    var transactionCode by remember { mutableStateOf("") }
    
    val userRole = SessionManager.roleID ?: "2"
    val companyId = SessionManager.accountID ?: "500"
    
    val scannerLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    val scans = result.data
        ?.extras
        ?.getSerializable("data")
        .let { it as? ArrayList<HashMap<String, String>> }
        ?.map { it["TYPE"].orEmpty() to it["VALUE"].orEmpty() }
        .orEmpty()

    // If we got at least one code, populate and show dialog
    scans.firstOrNull()?.second?.let { code ->
        transactionCode = code
        viewModelPayoutData.claimPayout(userID = companyId, roleID = userRole, barcodeResult = transactionCode)
    }
}

if(betResponse != null){
            
            }else if (betResponse == null && betResult == -1) {
            PrintBetPayoutErrorResults(betResult){
            viewModelPayoutData.clearBetState()
            }
            }

    // 1️⃣ Permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchSunmiScan(context, scannerLauncher)
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }
    
    LaunchedEffect(Unit) {
        // Connect websockets and hide connecting dialog when done
        viewModelDashboardData.connectWebSocket()
        viewModelStaffBetData.connectWebSocket()
        // Simplified: hide after a delay or based on a flag in ViewModel
        delay(2000L)
        showConnectingDialog = false
    }

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = listOf(DrawerScreen.liveBet, DrawerScreen.placeBet, DrawerScreen.claimPayout, DrawerScreen.cancelBet,
     DrawerScreen.currentBetsLog, DrawerScreen.withdrawDepositTicket, 
     DrawerScreen.transactionLog)
     
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val isDarkTheme = isSystemInDarkTheme()
    val iconTint = if (isDarkTheme) Color.White else Color.Black

    // Connecting to server progress dialog
    if (showConnectingDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Connecting to server...") },
            text = { Text("Please wait while the app establishes connection.") },
            confirmButton = {}
        )
    }

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
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

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

                // Bottom section: Log Out button
                TextButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = "Log Out", color = Color.White)
                }
            }
        }
    ) {
        Scaffold(
    topBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // typical AppBar height
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Open drawer",
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Sabong Betting",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { /* Do something for the end icon */ }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_scan_barcode),
                    contentDescription = "Scan Barcode",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            // On click: check permission or request then scan
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                launchSunmiScan(context, scannerLauncher)
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                    colorFilter = ColorFilter.tint(iconTint)
                )
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
       staffBet(staffData, dashboardData!!)
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
        testLayout()
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
        testLayout()
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
        testLayout()
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
        testLayout()
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
