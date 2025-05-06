package com.noveleta.sabongbetting.widgets;

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

import androidx.compose.material.icons.filled.ArrowDropDown

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

@Composable
fun CashTellerDialog(
    cashHandlers: List<CashHandlers>,
    cashInOrOut: String,
    onDismiss: () -> Unit,
    onConfirm: (cashAmount: String, selectedHandlerId: Int, password: String) -> Unit
) {
    var cashAmount by remember { mutableStateOf("") }
    var selectedHandlerIndex by remember { mutableStateOf(0) } // default first handler
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
    Button(
        onClick = {
            val selectedHandler = cashHandlers.getOrNull(selectedHandlerIndex)
            selectedHandler?.let {
                onConfirm(cashAmount, it.id, password)
            }
        },
        shape = RoundedCornerShape(100), // Pill Shape
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth() 
            .height(46.dp)  
    ) {
        Text(
            text = cashInOrOut,
            style = MaterialTheme.typography.labelLarge
        )
    }
},
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        title = {
            Text("Teller $cashInOrOut", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = cashAmount,
                    onValueChange = { cashAmount = it },
                    label = { Text("Cash Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Box {
                    OutlinedTextField(
                        value = cashHandlers.getOrNull(selectedHandlerIndex)?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Handler") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        cashHandlers.forEachIndexed { index, handler ->
                            DropdownMenuItem(
                                text = { Text(handler.name) },
                                onClick = {
                                    selectedHandlerIndex = index
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Cash Handler Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
            }
        }
    )
}

