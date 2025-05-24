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
import androidx.compose.ui.unit.toPx

import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import androidx.compose.ui.platform.LocalDensity

/*
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
*/

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

@Composable
fun EnterFormUI(viewModel: LoginViewModel, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val loginState by viewModel.loginState
    
    var userError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    
Box(
    modifier = Modifier
        .fillMaxSize()
) {
    
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    
    val scrollState = rememberScrollState()
val coroutineScope = rememberCoroutineScope()
val density = LocalDensity.current

val cardWidth = 280.dp
val cardWidthPx = with(LocalDensity.current) { cardWidth.toPx() }

val spacing = 16.dp
val totalCardWidth = cardWidth + spacing

val items = listOf(
    Triple("Meron", "200", Color(0xFFB12D36)),
    Triple("Draw", "204", Color(0xFF2EB132)),
    Triple("Wala", "120", Color(0xFF2070E1))
)

val currentIndex = remember { mutableStateOf(0) }

val totalCardWidthPx = with(density) { totalCardWidth.toPx() }
val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
val containerWidth = cardWidth  // So only one card is shown centered

fun scrollToCard(index: Int) {
    val centeredOffset = (totalCardWidthPx * index - (screenWidthPx - totalCardWidthPx) / 2).toInt()
    coroutineScope.launch {
        scrollState.animateScrollTo(
            centeredOffset.coerceIn(0, scrollState.maxValue),
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
    }
}

Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxWidth()
) {
    // LEFT arrow
    if (currentIndex.value > 0) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Scroll Left",
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    if (currentIndex.value > 0) {
                        currentIndex.value--
                        scrollToCard(currentIndex.value)
                    }
                },
            tint = Color.White
        )
    } else {
        Spacer(Modifier.size(28.dp))
    }

    // Scrollable Row
   val sidePadding = ((screenWidthPx - cardWidth.toPx()) / 2).toInt()

Box(
    modifier = Modifier
        .width(containerWidth)
        .horizontalScroll(scrollState)
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier = Modifier
            .padding(horizontal = with(LocalDensity.current) { sidePadding.toDp() })
    ) {
        items.forEach { (title, payout, color) ->
            BetInfoCards.InfoCard(
                title = title,
                payout = payout,
                totalBets = "0",
                backgroundColor = color,
                modifier = Modifier
                    .width(cardWidth)
                    .padding(vertical = 8.dp)
            )
        }
    }
}


    // RIGHT arrow
    if (currentIndex.value < items.lastIndex) {
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Scroll Right",
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    if (currentIndex.value < items.lastIndex) {
                        currentIndex.value++
                        scrollToCard(currentIndex.value)
                    }
                },
            tint = Color.White
        )
    } else {
        Spacer(Modifier.size(28.dp))
    }
}

    
  }
}


    /*Box(
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
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { showSettingsDialog = true },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
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

                         if (!userError && !passwordError) {
                            if (ipIsEmpty || portIsEmpty) {
                               showWarningDialog = true
                               } else {
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
                val code = (loginState as LoginState.Success).code
                when (code) {
                    1, 2 -> {
                        onSuccess()
                        viewModel.resetState()
                    }
                    6 -> {
                        Toast.makeText(context, "System Access is not y8et available!", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "Login Credentials are Invalid!", Toast.LENGTH_SHORT).show()
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
}*/

    
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
