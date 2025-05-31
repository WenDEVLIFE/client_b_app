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

suspend fun connectToServer(
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



}
