package com.noveleta.sabongbetting.Helper

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

@Composable
fun PrintMOHError(result: Int?, confirmButton: () -> Unit){

var resultText by remember { mutableStateOf("") }

when (result) {
    2 -> resultText = "ERROR! No System Name!"
    30 -> resultText = "ERROR! This Feature has been Closed by Admin."
    10 -> resultText = "ERROR! Comission Settings is not Active!"
}

AlertDialog(
      onDismissRequest = confirmButton,
      title = {
        Text(
          text = "Money On Hand Error",
          style = MaterialTheme.typography.headlineLarge
        )
      },
      text = {
        Text(
          text = "$resultText",
          fontSize = 12.sp,
          style = MaterialTheme.typography.bodyLarge
        )
      },
      confirmButton = {
        Button(
          onClick = confirmButton,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Okay",
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    )
}

@Composable
fun PrintBetErrorResults(result: Int?,messageBet: String?, confirmButton: () -> Unit){

var resultText by remember { mutableStateOf("") } 

if(result == 89){
resultText = "Login Error! Please logout and relogin your account!."
}else if(result == 99){
resultText = "Betting is not allowed at this Moment. Please Contact System Developer!."
}else if(result == 40){
resultText = "Your bet exceeds the allowed draw limit of $messageBet. Please enter a smaller amount."
}else if(result == 98){
resultText = "Betting is not allowed at this Moment. Please Contact System Developer!."
}else if(result == 5){
resultText = "ERROR! Unable to placed a BET. Please restart the application or Contact System Developer."
}else if(result == 6){
resultText = "ERROR! Unable to placed a BET. Betting is already CLOSED! or No Fight STARTED!"
}else if(result == 7){
resultText = "ERROR! Event is already closed!."
}else if(result == 8){
resultText = "ERROR! No event for today."
}else if(result == 11){
resultText = "ERROR! Fight Bettings for MERON is temporarily closed."
}else if(result == 12){
resultText = "ERROR! Fight Bettings for WALA is temporarily closed."
}else if(result == 13){
resultText = "ERROR! Fight Bettings for DRAW is temporarily closed."
}else if(result == 60){
resultText = "ERROR! Please Input Bet Amount, Minimum bet amount is 100"
}else if(result == 70){
resultText = "EROR! Please Input Bet Amount, Maximum bet amount is 50000."
}else if(result == 65){
resultText = "ERROR! Please Input Bet Amount, Minimum bet amount is 100"
}else if(result == 75){
resultText = "EROR! Please Input Bet Amount, Maximum bet amount is 500."
}



AlertDialog(
      onDismissRequest = confirmButton,
      title = {
        Text(
          text = "Mobile App Transaction",
          style = MaterialTheme.typography.headlineLarge
        )
      },
      text = {
        Text(
          text = "$resultText",
          fontSize = 12.sp,
          style = MaterialTheme.typography.bodyLarge
        )
      },
      confirmButton = {
        Button(
          onClick = confirmButton,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Okay",
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    )
}

@Composable
fun RePrintBetErrorResults(result: Int?, confirmButton: () -> Unit){

var resultText by remember { mutableStateOf("") } 

if(result == 1){
resultText = "ERROR! Bet Transaction with transaction code cannot be found!"
}else if(result == 2){
resultText = "ERROR! System name cannot be found. Please Contact System Developer!."
}


AlertDialog(
      onDismissRequest = confirmButton,
      title = {
        Text(
          text = "Mobile App Transaction",
          style = MaterialTheme.typography.headlineLarge
        )
      },
      text = {
        Text(
          text = "$resultText",
          fontSize = 12.sp,
          style = MaterialTheme.typography.bodyLarge
        )
      },
      confirmButton = {
        Button(
          onClick = confirmButton,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Okay",
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    )
}

@Composable
fun PrintBetPayoutErrorResults(result: Int?, confirmButton: () -> Unit){

var resultText by remember { mutableStateOf("Backend Error") }

when (result) {
    35 -> resultText = "ERROR! Invalid Barcode number."
    25 -> resultText = "ERROR! Bet status not updated, please contact system developer."
    55 -> resultText = "ERROR! There is an issue with claiming a bet, please contact system developer."
    28 -> resultText = "ERROR! Bet is already Claimed."
    18 -> resultText = "ERROR! Bet is already Claimed."
    24 -> resultText = "ERROR! Bet status not updated."
    27 -> resultText = "ERROR! Barcode too Short."
    26 -> resultText = "ERROR! Bet Unable to Update, Error(26)."
    0 -> resultText = "ERROR! Bet Failed to Refund, Error(0)."
    23 -> resultText = "ERROR! Bet is already returned."
    22 -> resultText = "ERROR! Payout unsuccessful, please contact system developer."
    21 -> resultText = "ERROR! Barcode is not the winning type, check it again and make sure it is the right one."
    20 -> resultText = "ERROR! Payout is not yet released."
    19 -> resultText = "ERROR! Result or Winner is not yet declared."
    16 -> resultText = "ERROR! Payout does not exist."
    17 -> resultText = "ERROR! Payout Bet is already Returned."
    7   -> resultText = "ERROR! Event is already closed. Error(7)"
    8   -> resultText = "ERROR! Event is already closed. Error(8)"
}



AlertDialog(
      onDismissRequest = confirmButton,
      title = {
        Text(
          text = "Mobile App Transaction",
          style = MaterialTheme.typography.headlineLarge
        )
      },
      text = {
        Text(
          text = "$resultText",
          fontSize = 12.sp,
          style = MaterialTheme.typography.bodyLarge
        )
      },
      confirmButton = {
        Button(
          onClick = confirmButton,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Okay",
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    )
}

@Composable
fun PrintCancelBetErrorResults(result: Int?, confirmButton: () -> Unit){

var resultText by remember { mutableStateOf("") }

when (result) {
    30 -> resultText = "ERROR! Ticket bet is already cancelled."
    31 -> resultText = "ERROR! Unable to cancel the ticket bet."
    33 -> resultText = "ERROR! Cancellation only allowed if fight is open or on last call."
    34 -> resultText = "ERROR! Ticket bet does not exist."
    0  -> resultText = "ERROR! Transaction logging failed."
}

AlertDialog(
      onDismissRequest = confirmButton,
      title = {
        Text(
          text = "Mobile App Transaction",
          style = MaterialTheme.typography.headlineLarge
        )
      },
      text = {
        Text(
          text = "$resultText",
          fontSize = 12.sp,
          style = MaterialTheme.typography.bodyLarge
        )
      },
      confirmButton = {
        Button(
          onClick = confirmButton,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Okay",
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    )
}


@Composable
fun PrintWithdrawErrorResults(result: Int?, confirmButton: () -> Unit){

var resultText by remember { mutableStateOf("") }

when (result) {
    2 -> resultText = "ERROR! Insufficient points, Withdrawal request has been cancelled."
    3 -> resultText = "ERROR! Barcode has already been used and withdrawed."
    4 -> resultText = "ERROR! Barcode was already cancelled due to insufficient points."
    5 -> resultText = "ERROR! Barcode does not exist!"
    7 -> resultText = "ERROR! Event is already closed!"
    8  -> resultText = "ERROR! No event for today."
}



AlertDialog(
      onDismissRequest = confirmButton,
      title = {
        Text(
          text = "Mobile App Transaction",
          style = MaterialTheme.typography.headlineLarge
        )
      },
      text = {
        Text(
          text = "$resultText",
          fontSize = 12.sp,
          style = MaterialTheme.typography.bodyLarge
        )
      },
      confirmButton = {
        Button(
          onClick = confirmButton,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Okay",
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    )
}

@Composable
fun PrintDepositErrorResults(result: Int?, confirmButton: () -> Unit){

var resultText by remember { mutableStateOf("") }

when (result) {
    2 -> resultText = "ERROR! Unable to deposit your requested points. Please contact the system administrator."
    3 -> resultText = "ERROR! Barcode has already been used and deposited."
    4 -> resultText = "ERROR! Barcode does not exist!"
    7 -> resultText = "ERROR! Event is already closed!"
    8  -> resultText = "ERROR! No event for today."
}

AlertDialog(
      onDismissRequest = confirmButton,
      title = {
        Text(
          text = "Mobile App Transaction",
          style = MaterialTheme.typography.headlineLarge
        )
      },
      text = {
        Text(
          text = "$resultText",
          fontSize = 12.sp,
          style = MaterialTheme.typography.bodyLarge
        )
      },
      confirmButton = {
        Button(
          onClick = confirmButton,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Okay",
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    )
}

/*
---------------------------

TODO:: CREATE TELLER CASH IN CASH OUT ERRORS CODE AND RESULTS

---------------------------
*/

@Composable
fun PrintTellerCashOutErrorResults(result: Int?, confirmButton: () -> Unit){

var resultText by remember { mutableStateOf("") }


when (result) {
    15 -> resultText = "ERROR! Cash Out Failed, Failed to process cash out."
    7 -> resultText = "ERROR! Event is Closed"
    8 -> resultText = "ERROR! No active event found."
    14 -> resultText = "ERROR! Cash handler password is incorrect!"
    7 -> resultText = "ERROR! Event is already closed!"
    10  -> resultText = "ERROR! Empty Company ID."
}


AlertDialog(
      onDismissRequest = confirmButton,
      title = {
        Text(
          text = "Mobile App Transaction",
          style = MaterialTheme.typography.headlineLarge
        )
      },
      text = {
        Text(
          text = "$resultText",
          fontSize = 12.sp,
          style = MaterialTheme.typography.bodyLarge
        )
      },
      confirmButton = {
        Button(
          onClick = confirmButton,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Okay",
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    )
}

@Composable
fun PrintTellerCashInErrorResults(result: Int?, confirmButton: () -> Unit){

var resultText by remember { mutableStateOf("") }

when (result) {
    6 -> resultText = "ERROR! Unable to save cash-in transaction. Please contact the system administrator."
    7 -> resultText = "ERROR! No active event available!"
    9 -> resultText = "ERROR! No event found!"
    4 -> resultText = "ERROR! Cash handler password is incorrect!"
    10 -> resultText = "ERROR! No System Name Found. Please contact the system administrator."
    20 -> resultText = "ERROR! Invalid Json Input!"
}

AlertDialog(
      onDismissRequest = confirmButton,
      title = {
        Text(
          text = "Mobile App Transaction",
          style = MaterialTheme.typography.headlineLarge
        )
      },
      text = {
        Text(
          text = "$resultText",
          fontSize = 12.sp,
          style = MaterialTheme.typography.bodyLarge
        )
      },
      confirmButton = {
        Button(
          onClick = confirmButton,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Okay",
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    )
}

