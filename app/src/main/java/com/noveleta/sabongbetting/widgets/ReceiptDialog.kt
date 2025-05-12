package com.noveleta.sabongbetting.widgets;

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.SharedPreference.*
import com.noveleta.sabongbetting.R
import com.noveleta.sabongbetting.*
import com.noveleta.sabongbetting.Helper.*

@Composable
fun BetReceiptDialog(
    response: BetResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        title = { Text("Print Bet Receipt") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 320.dp, height = 380.dp)
            ) {
                // Barcode display
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            val bitmap = generateBarcodeBitmap(response.barcode, 300, 100)
                            setImageBitmap(bitmap)
                        }
                    },
                    modifier = Modifier.size(200.dp)
                )

                Spacer(Modifier.height(8.dp))
                Text(text = response.barcode, fontSize = 15.sp)
                Text(text = response.transactionDate, fontSize = 13.sp)
                Text(text = response.systemName, fontSize = 13.sp)
                Text(
                    text = "Cashier: ${response.cashier}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(text = "BET", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "AMOUNT: ${"%,.0f".format(response.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "FIGHT #: ${response.fightNumber}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if(response.betType == 1){
                Text(
                    text = "SIDE: MERON",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                }else if(response.betType == 2){
                Text(
                    text = "SIDE: WALA",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                }else{
                Text(
                    text = "SIDE: DRAW",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun ReprintBetReceiptDialog(
    response: ReprintBetResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        title = { Text("Print Bet Receipt") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 320.dp, height = 380.dp)
            ) {
                // Barcode display
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            val bitmap = generateBarcodeBitmap(response.barcode, 300, 100)
                            setImageBitmap(bitmap)
                        }
                    },
                    modifier = Modifier.size(200.dp)
                )

                Spacer(Modifier.height(8.dp))
                Text(text = response.barcode, fontSize = 15.sp)
                Text(text = response.transactionDate, fontSize = 13.sp)
                Text(text = response.systemName, fontSize = 13.sp)
                Text(
                    text = "Cashier: ${response.cashier}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(text = "RERPINT BET", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "AMOUNT: ${response.amount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "FIGHT #: ${response.fightNumber}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "SIDE: ${response.betType}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun PayoutReceiptDialog(
    response: BetPayoutResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        title = { Text("Print Payout Receipt") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 320.dp, height = 380.dp)
            ) {
            // Barcode display
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            val bitmap = generateBarcodeBitmap(response.barcode, 300, 100)
                            setImageBitmap(bitmap)
                        }
                    },
                    modifier = Modifier.size(200.dp)
                )

                Spacer(Modifier.height(8.dp))
                Text(text = response.transactionCode, fontSize = 15.sp)
                Text(text = response.transactionDate, fontSize = 13.sp)
                Text(text = response.systemName, fontSize = 13.sp)
                Text(
                    text = "Cashier: ${response.transactionCashier}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(text = "PAYOUT", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Fight #: ${response.fightNumber}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if(response.betType == 1){
                Text(
                    text = "SIDE: MERON",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                }else if(response.betType == 2){
                Text(
                    text = "SIDE: WALA",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                }else{
                Text(
                    text = "SIDE: DRAW",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                }
                Text(
                    text = "AMOUNT: ${response.amount}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "ODDS: ${response.odds}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "PAYOUT: ${response.payout}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun TellerFundCashOutReceiptDialog(
    response: CashoutResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        title = { Text("Print Cash Out Receipt") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 320.dp, height = 380.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                Text(text = response.transactionDate, fontSize = 13.sp)
                Text(text = response.systemName, fontSize = 13.sp)
                Text(
                    text = "Cashier: ${response.cashier}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(text = "Cash Out", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "AMOUNT: ${response.cashoutAmount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Cash Handler Name: ${response.cashHandler}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun TellerFundCashInReceiptDialog(
    response: CashinResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        title = { Text("Print Cash In Receipt") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 320.dp, height = 380.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                Text(text = response.transactionDate, fontSize = 13.sp)
                Text(text = response.systemName, fontSize = 13.sp)
                Text(
                    text = "Cashier: ${response.cashierUsername}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(text = "Cash In", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "AMOUNT: ${response.cashinAmount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Cash Handler Name: ${response.cashinHandlerUsername}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun CancelReceiptDialog(
    response: CancelledBetResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        title = { Text("Print Cancel Receipt") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 320.dp, height = 380.dp)
            ) {
            // Barcode display
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            val bitmap = generateBarcodeBitmap(response.barcode, 300, 100)
                            setImageBitmap(bitmap)
                        }
                    },
                    modifier = Modifier.size(200.dp)
                )
                
                Spacer(Modifier.height(8.dp))
                Text(
                    text = response.barcode,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = response.dateTime, fontSize = 14.sp)
                Text(text = response.systemName, fontSize = 14.sp)
                Text(
                    text = "Cashier: ${response.cashier}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(text = "CANCELLED TICKET BET", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "FIGHT #: ${response.fightNumber}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "SIDE: ${response.side}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "AMOUNT: ${response.amount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun MobileWithdrawReceiptDialog(
    response: MobileWithdrawResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        title = { Text("Print Mobile Deposit Receipt") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 320.dp, height = 380.dp)
            ) {
            
            // Barcode display
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            val bitmap = generateBarcodeBitmap(response.barcode, 300, 100)
                            setImageBitmap(bitmap)
                        }
                    },
                    modifier = Modifier.size(200.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = response.barcode,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(text = response.timestamp, fontSize = 14.sp)
                Text(text = response.systemName, fontSize = 14.sp)
                Text(
                    text = "Cashier: ${response.cashier}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(text = "POINTS WITHDRAW", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "ACCOUNT: ${response.accountID}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "CURRENT POINTS: ${response.currentBalance}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "WITHDRAW AMOUNT: ${response.withdrawnAmount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "AVAILABLE POINTS: ${response.newBalance}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Okay")
            }
        }
    )
}

@Composable
fun MobileDepositReceiptDialog(
    response: MobileDepositResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        title = { Text("Print Mobile Deposit Receipt") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 320.dp, height = 380.dp)
            ) {
            
            // Barcode display
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            val bitmap = generateBarcodeBitmap(response.barcode, 300, 100)
                            setImageBitmap(bitmap)
                        }
                    },
                    modifier = Modifier.size(200.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = response.barcode,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(text = response.datetime, fontSize = 14.sp)
                Text(text = response.systemName, fontSize = 14.sp)
                Text(
                    text = "Cashier: ${response.cashier}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(text = "POINTS DEPOSITED", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "ACCOUNT: ${response.accountID}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "CURRENT POINTS: ${response.currentBalance}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "DEPOSIT AMOUNT: ${response.depositAmount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "AVAILABLE POINTS: ${response.availablePoints}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Okay")
            }
        }
    )
}