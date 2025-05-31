package com.noveleta.sabongbetting.ui


import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.Network.*
import com.noveleta.sabongbetting.Dashboard.*
import com.noveleta.sabongbetting.Enter.*
import com.noveleta.sabongbetting.*

import android.app.Activity
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.ui.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import android.content.Intent
import android.provider.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    networkMonitor: NetworkMonitor,
    viewModelFactory: ViewModelProvider.Factory
) {

    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    

    val isConnected by networkMonitor.isConnected.collectAsState()

    var showBanner by remember { mutableStateOf(false) }
    var bannerMessage by remember { mutableStateOf("") }
    var bannerColor by remember { mutableStateOf(Color.Red) }

    val animatedColor by animateColorAsState(
        targetValue = bannerColor,
        animationSpec = tween(durationMillis = 500),
        label = "Banner Color Animation"
    )
    
    var showOfflineDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            bannerMessage = "You are offline"
            bannerColor = Color.Red
            showBanner = true
            showOfflineDialog = true
        } else if (showBanner) {
            bannerMessage = "Back online"
            bannerColor = Color(0xFF1BEB53)
            showBanner = true
            showOfflineDialog = false
            delay(2000)
            showBanner = false
        }
    } 
    

if (showOfflineDialog) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text("No Internet Connection")
        },
        text = {
            Text("You are currently offline. Please reconnect or quit the app.")
        },
        confirmButton = {
            TextButton(onClick = {
                showOfflineDialog = false
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                context.startActivity(intent)
            }) {
                Text("Reconnect")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                showOfflineDialog = false
                if (context is Activity) {
                    context.finishAffinity() // Quits the app
                }
            }) {
                Text("Quit")
            }
        }
    )
}


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AnimatedVisibility(
                visible = showBanner,
                enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(300)),
                exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(animatedColor)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bannerMessage,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("login") {
                    val vm: LoginViewModel = viewModel(factory = viewModelFactory)
                    EnterFormUI(vm, networkMonitor) {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                composable("main") {
                    MainWithDrawer(networkMonitor)
                }
        }
    }
}
