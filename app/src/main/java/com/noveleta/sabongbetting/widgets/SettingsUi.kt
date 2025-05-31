package com.noveleta.sabongbetting.widgets

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

import java.net.Inet4Address
import java.net.NetworkInterface

import com.noveleta.sabongbetting.SharedPreference.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.ui.theme.*

object SettingsUi {

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    initialIp: String = "",
    initialPort: String = ""
) {
    var ipAddress by remember { mutableStateOf(initialIp) }
    var portNumber by remember { mutableStateOf(initialPort) }
    var targetPosIp by remember { mutableStateOf("") }
var targetPosPort by remember { mutableStateOf("") }

    fun getLocalIpAddress(): String? {
    try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val intf = interfaces.nextElement()
            val addresses = intf.inetAddresses
            while (addresses.hasMoreElements()) {
                val addr = addresses.nextElement()
                if (!addr.isLoopbackAddress && addr is Inet4Address) {
                    return addr.hostAddress
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace() // Or Log.e("IP", "Error getting IP", ex)
    }
    return null
}

    /*
    // ------------------------------------
    // 
    // IP ADDRESS DISPLAY FOR SUNMI DEVICES SIDE
    //
    // ------------------------------------
    */
    
    var localIpAddress = remember { getLocalIpAddress() ?: "Unavailable" }
    var localPort = "8080"
    
    
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val textFieldColors = TextFieldDefaults.colors(
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedIndicatorColor = textColor,
    unfocusedIndicatorColor = textColor.copy(alpha = 0.5f),
    focusedPlaceholderColor = textColor.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = textColor.copy(alpha = 0.5f)
)


    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_ios),
                    contentDescription = "Back",
                    tint = textColor,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onDismiss() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Settings", color = textColor)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                            .verticalScroll(rememberScrollState())
            ) {
                Text(text = "IP Address", color = textColor)
                TextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    placeholder = { Text("192.168.8.xxx", color = textColor.copy(alpha = 0.5f)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                Text(text = "Port Number", color = textColor)
                TextField(
                    value = portNumber,
                    onValueChange = { portNumber = it },
                    placeholder = { Text("808...", color = textColor.copy(alpha = 0.5f)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                
Divider(thickness = 1.dp, color = textColor.copy(alpha = 0.2f))
Spacer(modifier = Modifier.height(12.dp))
Text(
    text = "Connect to Sunmi POS Device (For Android Devices only)",
    color = MaterialTheme.colorScheme.primary,
    style = MaterialTheme.typography.titleMedium
)
Spacer(modifier = Modifier.height(8.dp))

Text(text = "Target POS IP Address", color = textColor)
TextField(
    value = targetPosIp,
    onValueChange = { targetPosIp = it },
    placeholder = { Text("e.g. 192.168.8.101", color = textColor.copy(alpha = 0.5f)) },
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
    colors = textFieldColors
)

Text(text = "Target POS Port Number", color = textColor)
TextField(
    value = targetPosPort,
    onValueChange = { targetPosPort = it },
    placeholder = { Text("e.g. 8080", color = textColor.copy(alpha = 0.5f)) },
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
    colors = textFieldColors
)
Text(
    text = "IP Address POS Server (For POS Devices Only)",
    color = MaterialTheme.colorScheme.primary,
    style = MaterialTheme.typography.titleMedium
)

Text(text = "POS Device IP Address", color = textColor)

TextField(
    value = localIpAddress,
    onValueChange = { localIpAddress = it },
    placeholder = { Text("192.168.8.xxx", color = textColor.copy(alpha = 0.5f)) },
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
    colors = textFieldColors
)

Text(text = "POS Device Port Number", color = textColor)
TextField(
    value = localPort,
    onValueChange = { localPort = it },
    placeholder = { Text("808..", color = textColor.copy(alpha = 0.5f)) },
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
    colors = textFieldColors
)

            }
        },
        confirmButton = {
            TextButton(onClick = {
                SessionManager.ipAddress = ipAddress
                SessionManager.portAddress = portNumber
                SessionManager.posIpAddress = targetPosIp
                SessionManager.posPortAddress = targetPosPort
                onDismiss()
            }) {
                Text("Save", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}


}


