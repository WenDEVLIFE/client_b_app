package com.noveleta.sabongbetting.Enter;

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
import androidx.compose.ui.draw.*
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
import androidx.compose.ui.platform.LocalContext

import android.widget.Toast

import coil.compose.AsyncImage

import com.google.accompanist.systemuicontroller.rememberSystemUiController

import kotlinx.coroutines.*

import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.SharedPreference.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.Network.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.*

import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size

import androidx.compose.runtime.collectAsState

@Composable
fun EnterFormUI(viewModel: LoginViewModel, networkMonitor: NetworkMonitor, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val loginState by viewModel.loginState
    
    var userError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showPOSWarningDialog by remember { mutableStateOf(false) }
    var showPOSAuthenticationDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    val isSunmi = SessionManager.isSunmiDevice
    
    val isConnected by networkMonitor.isConnected.collectAsState()

    var signalLevel by remember { mutableStateOf(0) }

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
    
    Box(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
) {
    CoilBackground()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    
    if (SessionManager.isSunmiDevice) {
        IconButton(
            onClick = { showInfoDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = Color.White
            )
        }
    }

    Spacer(modifier = Modifier.weight(1f))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "WiFi Signal Strength",
            modifier = Modifier.size(24.dp),
            tint = if (isConnected) Color.Green else Color.Red
        )

        Spacer(modifier = Modifier.width(8.dp)) // space between icons

        IconButton(
            onClick = { showSettingsDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White
            )
        }
    }
}


        
        Spacer(modifier = Modifier.height(80.dp))
        
        Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier.size(110.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Log In",
                fontSize =23.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditTextWidgets.editTextOneLine(
                    placeHolderTitle = "Username",
                    textValue = viewModel.userName.value,
                    onTextValueChange = { viewModel.userName.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    isPasswordField = false,
                    isError = userError
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                EditTextWidgets.editTextOneLine(
                    placeHolderTitle = "Password",
                    textValue = viewModel.password.value,
                    onTextValueChange = { viewModel.password.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    isPasswordField = true,
                    isError = passwordError
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (loginState is LoginState.Error) {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }else if(passwordError){
                Text(
                    text = "Password cannot be empty!",
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }else if(userError){
                Text(
                    text = "Username cannot be empty!",
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }else if(userError && passwordError){
                Text(
                    text = "Username and Password cannot be empty!",
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (loginState != LoginState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(
                            color = if (viewModel.userName.value.isNotBlank() && viewModel.password.value.isNotBlank())
                                Color(0xFF3E65FF) else Color(0xFF3F4F8A),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .clickable {
                         userError = viewModel.userName.value.isBlank()
                         passwordError = viewModel.password.value.isBlank()

                         val ipIsEmpty = SessionManager.ipAddress.isNullOrBlank()
                         val portIsEmpty = SessionManager.portAddress.isNullOrBlank()
                         val ip = SessionManager.posIpAddress ?: ""
                         val port = SessionManager.posPortAddress ?: ""
                         
                         if (!userError && !passwordError) {
                            if (ipIsEmpty || portIsEmpty) {
                               showWarningDialog = true
                               }else if (ip.isNullOrBlank() && port.isNullOrBlank()) {
                               showPOSWarningDialog = true
                               }else {
                               viewModel.logInUser()
                             }
                           }
                       },    
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sign In", fontSize = 12.sp,fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(36.dp))
            }

            /*Text(
                text = buildAnnotatedString {
                    append("Create New Account as a New User")
                    addStyle(
                        SpanStyle(textDecoration = TextDecoration.Underline, color = Color(0xFFDB535A)),
                        start = 0,
                        end = 32
                    )
                },
                fontSize = 11.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    },
                textAlign = TextAlign.Center
            )*/
            
            }
        }

        // Handle navigation on success, with a LaunchedEffect
        LaunchedEffect(loginState) {
    if (loginState is LoginState.Success) {
        val code = loginState.code
        when (code) {
            1, 2 -> {
                if (!SessionManager.isSunmiDevice) {
                    val ip = SessionManager.posIpAddress ?: ""
                    val port = SessionManager.posPortAddress ?: ""

                    if (ip.isNotBlank() && port.isNotBlank()) {
                        sendConnectedStatusToPOS(
                            ip = ip,
                            port = port,
                            username = SessionManager.cname ?: "",
                            password = SessionManager.userpassword ?: "",
                            onAuthFailed = {
                                showPOSAuthenticationDialog = true
                            },
                            onAuthSuccess = {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Connected to POS successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onSuccess()
                                    viewModel.resetState()
                                }
                            }
                        )
                    } else {
                        // Show a message or dialog that POS IP or port is not set
                        showPOSWarningDialog = true
                    }
                }else{
                    onSuccess()
                    viewModel.resetState()
                }
            }

            6 -> {
                Toast.makeText(context, "System Access is not yet available!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }

            8 -> {
                Toast.makeText(context, "No event for today. Access to system is limited.", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }

            10 -> {
                Toast.makeText(context, "Server might be offline, please try again later.", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }

            else -> {
                Toast.makeText(context, "Login credentials are invalid!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
        }
    }
 }

}
    
    var ip = SessionManager.ipAddress
    val port = SessionManager.portAddress

    if (showSettingsDialog) {
        if (ip.isNullOrEmpty()) {
            ip = getWifiIpAddress(context)
        }
    SettingsUi.SettingsDialog(
        onDismiss = { showSettingsDialog = false },
        initialIp = ip ?: "",
        initialPort = port ?: ""
    )
}

if (showInfoDialog){
   SettingsUi.SunmiInfoDialog(
   onDismiss = { showInfoDialog = false }
   )
}

if (showWarningDialog) {
    AlertDialog(
        onDismissRequest = { showWarningDialog = false },
        confirmButton = {
            TextButton(
                onClick = {
                    showWarningDialog = false
                    showSettingsDialog = true
                }
            ) {
                Text("Go to Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = { showWarningDialog = false }) {
                Text("Cancel")
            }
        },
        title = { Text("Missing Configuration!") },
        text = { Text("Please set up your IP address and port number before logging in.") }
    )
}

if (showPOSWarningDialog) {
    AlertDialog(
        onDismissRequest = { showPOSWarningDialog = false },
        confirmButton = {
            TextButton(
                onClick = {
                    showPOSWarningDialog = false
                    showSettingsDialog = true
                }
            ) {
                Text("Go to Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = { showPOSWarningDialog = false }) {
                Text("Cancel")
            }
        },
        title = { Text("Missing POS Configuration!") },
        text = { Text("Please set up your POS IP address and port number before logging in.") }
    )
}

 if (showPOSAuthenticationDialog){
     AlertDialog(
        onDismissRequest = { showPOSAuthenticationDialog = false },
        confirmButton = {
            TextButton(
                onClick = {
                    showPOSAuthenticationDialog = false
                }
            ) {
                Text("Okay")
            }
        },
        dismissButton = {
            TextButton(onClick = { showPOSAuthenticationDialog = false }) {
                Text("Cancel")
            }
        },
        title = { Text("POS Login failed!") },
        text = { Text("ERROR! Authentication failed. Username and password do not match the POS account!") }
    )
 }

    
}

@Composable
fun CoilBackground() {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(R.drawable.bg)
            .size(Size.ORIGINAL)
            .allowHardware(false)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(800.dp) // show only part of the background
    )
}
