package com.noveleta.sabongbetting.Api;

import com.noveleta.sabongbetting.ui.theme.*
import com.noveleta.sabongbetting.ui.*
import com.noveleta.sabongbetting.Factory.*
import com.noveleta.sabongbetting.Model.*
import com.noveleta.sabongbetting.Helper.*
import com.noveleta.sabongbetting.Api.*
import com.noveleta.sabongbetting.widgets.*
import com.noveleta.sabongbetting.Network.*
import com.noveleta.sabongbetting.Enter.*
import com.noveleta.sabongbetting.*

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.statement.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

object printWebsocketPOS {

private val client = HttpClient(CIO) {
    install(WebSockets)
}

suspend fun sendConnectedStatusToPOS(
    ip: String,
    port: String,
    username: String,
    password: String,
    onAuthFailed: suspend () -> Unit,
    onAuthSuccess: suspend () -> Unit,
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val connectPayload = BarcodePayload(
            from = "phone",
            type = "connected",
            data = "Client connected successfully",
            username = username,
            password = password
        )

        val connectJson = json.encodeToString(connectPayload)
        send(Frame.Text(connectJson))
        println("Sent connection notification: $connectJson")

        // Listen for responses
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")

                    if (received.type == "error" && received.data.contains("Authentication failed", ignoreCase = true)) {
                        println("Authentication failed message received")
                        onAuthFailed() 
                        break
                    }
                    
                    if (received.type == "acknowledge" && received.data.contains("Authenticated and connected", ignoreCase = true)) {
                        println("Authentication connected message received")
                        onAuthSuccess() 
                        break
                    }

                   
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}


suspend fun sendPayoutPrint(
    ip: String,
    port: String,
    payoutResponse: BetPayoutResponse,
    username: String,
    password: String
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val payload: BarcodePayload = if (payoutResponse != null) {
            // Serialize BetPayoutResponse and wrap it in BarcodePayload
            val payoutJson = json.encodeToString(payoutResponse)
            BarcodePayload(
                from = "phone",
                type = "payoutbetresponse",
                data = payoutJson,
                username = username,
                password = password
            )
        } else {
            // Fallback test payload
            BarcodePayload(
                from = "phone",
                type = "barcode",
                data = "1234567890",
                username = username,
                password = password
            )
        }

        val jsonString = json.encodeToString(payload)
        send(Frame.Text(jsonString))
        println("Sent: $jsonString")

        // Receive loop
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}

suspend fun sendCancelBetPrint(
    ip: String,
    port: String,
    payoutResponse: CancelledBetResponse,
    username: String,
    password: String
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val payload: BarcodePayload = if (payoutResponse != null) {
            // Serialize BetPayoutResponse and wrap it in BarcodePayload
            val payoutJson = json.encodeToString(payoutResponse)
            BarcodePayload(
                from = "phone",
                type = "cancelbetresponse",
                data = payoutJson,
                username = username,
                password = password
            )
        } else {
            // Fallback test payload
            BarcodePayload(
                from = "phone",
                type = "barcode",
                data = "1234567890",
                username = username,
                password = password
            )
        }

        val jsonString = json.encodeToString(payload)
        send(Frame.Text(jsonString))
        println("Sent: $jsonString")

        // Receive loop
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}

suspend fun sendReprintBet(
    ip: String,
    port: String,
    payoutResponse: ReprintBetResponse,
    username: String,
    password: String
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val payload: BarcodePayload = if (payoutResponse != null) {
            // Serialize BetPayoutResponse and wrap it in BarcodePayload
            val payoutJson = json.encodeToString(payoutResponse)
            BarcodePayload(
                from = "phone",
                type = "reprintbetresponse",
                data = payoutJson,
                username = username,
                password = password
            )
        } else {
            // Fallback test payload
            BarcodePayload(
                from = "phone",
                type = "barcode",
                data = "1234567890",
                username = username,
                password = password
            )
        }

        val jsonString = json.encodeToString(payload)
        send(Frame.Text(jsonString))
        println("Sent: $jsonString")

        // Receive loop
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}

suspend fun sendMobileWithdrawPrint(
    ip: String,
    port: String,
    payoutResponse: MobileWithdrawResponse,
    username: String,
    password: String
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val payload: BarcodePayload = if (payoutResponse != null) {
            // Serialize BetPayoutResponse and wrap it in BarcodePayload
            val payoutJson = json.encodeToString(payoutResponse)
            BarcodePayload(
                from = "phone",
                type = "mobilewithddrawresponse",
                data = payoutJson,
                username = username,
                password = password
            )
        } else {
            // Fallback test payload
            BarcodePayload(
                from = "phone",
                type = "barcode",
                data = "1234567890",
                username = username,
                password = password
            )
        }

        val jsonString = json.encodeToString(payload)
        send(Frame.Text(jsonString))
        println("Sent: $jsonString")

        // Receive loop
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}

suspend fun sendMobileDepositPrint(
    ip: String,
    port: String,
    payoutResponse: MobileDepositResponse,
    username: String,
    password: String
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val payload: BarcodePayload = if (payoutResponse != null) {
            // Serialize BetPayoutResponse and wrap it in BarcodePayload
            val payoutJson = json.encodeToString(payoutResponse)
            BarcodePayload(
                from = "phone",
                type = "mobiledepositresponse",
                data = payoutJson,
                username = username,
                password = password
            )
        } else {
            // Fallback test payload
            BarcodePayload(
                from = "phone",
                type = "barcode",
                data = "1234567890",
                username = username,
                password = password
            )
        }

        val jsonString = json.encodeToString(payload)
        send(Frame.Text(jsonString))
        println("Sent: $jsonString")

        // Receive loop
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}

suspend fun sendBetResponsePrint(
    ip: String,
    port: String,
    payoutResponse: BetResponse,
    username: String,
    password: String
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val payload: BarcodePayload = if (payoutResponse != null) {
            // Serialize BetPayoutResponse and wrap it in BarcodePayload
            val payoutJson = json.encodeToString(payoutResponse)
            BarcodePayload(
                from = "phone",
                type = "betresponse",
                data = payoutJson,
                username = username,
                password = password
            )
        } else {
            // Fallback test payload
            BarcodePayload(
                from = "phone",
                type = "barcode",
                data = "1234567890",
                username = username,
                password = password
            )
        }

        val jsonString = json.encodeToString(payload)
        send(Frame.Text(jsonString))
        println("Sent: $jsonString")

        // Receive loop
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}

suspend fun sendMoneyOnHandReport(
    ip: String,
    port: String,
    payoutResponse: SummaryReport,
    username: String,
    password: String
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val payload: BarcodePayload = if (payoutResponse != null) {
            // Serialize BetPayoutResponse and wrap it in BarcodePayload
            val payoutJson = json.encodeToString(payoutResponse)
            BarcodePayload(
                from = "phone",
                type = "mohreport",
                data = payoutJson,
                username = username,
                password = password
            )
        } else {
            // Fallback test payload
            BarcodePayload(
                from = "phone",
                type = "barcode",
                data = "1234567890",
                username = username,
                password = password
            )
        }

        val jsonString = json.encodeToString(payload)
        send(Frame.Text(jsonString))
        println("Sent: $jsonString")

        // Receive loop
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}

suspend fun sendCashOutTeller(
    ip: String,
    port: String,
    payoutResponse: CashoutResponse,
    username: String,
    password: String
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val payload: BarcodePayload = if (payoutResponse != null) {
            // Serialize BetPayoutResponse and wrap it in BarcodePayload
            val payoutJson = json.encodeToString(payoutResponse)
            BarcodePayload(
                from = "phone",
                type = "cashouttellerresponse",
                data = payoutJson,
                username = username,
                password = password
            )
        } else {
            // Fallback test payload
            BarcodePayload(
                from = "phone",
                type = "barcode",
                data = "1234567890",
                username = username,
                password = password
            )
        }

        val jsonString = json.encodeToString(payload)
        send(Frame.Text(jsonString))
        println("Sent: $jsonString")

        // Receive loop
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}

suspend fun sendCashInTeller(
    ip: String,
    port: String,
    payoutResponse: CashinResponse,
    username: String,
    password: String
) {
    val json = Json { ignoreUnknownKeys = true }

    client.webSocket("ws://$ip:$port/ws") {
        val payload: BarcodePayload = if (payoutResponse != null) {
            // Serialize BetPayoutResponse and wrap it in BarcodePayload
            val payoutJson = json.encodeToString(payoutResponse)
            BarcodePayload(
                from = "phone",
                type = "cashintellerresponse",
                data = payoutJson,
                username = username,
                password = password
            )
        } else {
            // Fallback test payload
            BarcodePayload(
                from = "phone",
                type = "barcode",
                data = "1234567890",
                username = username,
                password = password
            )
        }

        val jsonString = json.encodeToString(payload)
        send(Frame.Text(jsonString))
        println("Sent: $jsonString")

        // Receive loop
        for (message in incoming) {
            if (message is Frame.Text) {
                val text = message.readText()
                try {
                    val received = json.decodeFromString<BarcodePayload>(text)
                    println("From: ${received.from}, Type: ${received.type}, Data: ${received.data}")
                } catch (e: Exception) {
                    println("Received non-decodable message: $text")
                }
            }
        }
    }
}



}
