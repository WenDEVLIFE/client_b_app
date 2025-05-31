package com.noveleta.sabongbetting.Api

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

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import fi.iki.elonen.NanoWSD
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD.WebSocketFrame
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

// Android Context
import android.content.Context

// Kotlin Coroutines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WebsocketServerPOS {
    private var server: POSWebSocketServer? = null

    fun start(context: Context, port: Int = 8080) {
        if (server != null) return

        server = POSWebSocketServer(port, context.applicationContext)
        try {
            server!!.start()
            println("POS WebSocket server started on port $port")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stop() {
        server?.stop()
        server = null
        println("POS WebSocket server stopped")
    }

    private class POSWebSocketServer(
        port: Int,
        private val context: Context
    ) : NanoWSD(port) {
        override fun openWebSocket(handshake: NanoHTTPD.IHTTPSession?): WebSocket {
            return POSWebSocket(handshake, context)
        }
    }

    private class POSWebSocket(
        handshakeRequest: NanoHTTPD.IHTTPSession?,
        val context: Context
    ) : NanoWSD.WebSocket(handshakeRequest) {
        private val json = Json { ignoreUnknownKeys = true }

        override fun onOpen() {
            println("Client connected")
        }

        override fun onClose(code: WebSocketFrame.CloseCode?, reason: String?, initiatedByRemote: Boolean) {
            println("Client disconnected: $reason")
        }

        override fun onMessage(message: WebSocketFrame) {
            val text = message.textPayload
            println("Received message: $text")
            try {
                val payload = json.decodeFromString<BarcodePayload>(text)
                println("Parsed from: ${payload.from}, type: ${payload.type}, data: ${payload.data}")

                when (payload.type) {
                    "barcode" -> {
                        val response = payload.copy(from = "pos")
                        send(json.encodeToString(response))
                    }

                    "payoutbetresponse" -> {
                        try {
                            val payoutResponse = json.decodeFromString<BetPayoutResponse>(payload.data)

                            CoroutineScope(Dispatchers.IO).launch {
                                printPayout(context, payoutResponse)
                            }

                            send(json.encodeToString(payload.copy(from = "pos", data = "payout printed")))
                        } catch (e: Exception) {
                            println("Error parsing payoutbetresponse: ${e.localizedMessage}")
                            send(json.encodeToString(payload.copy(from = "pos", data = "invalid payout response")))
                        }
                    }

                    else -> {
                        send("""{"from":"pos","type":"error","data":"Unknown type"}""")
                    }
                }
            } catch (e: Exception) {
                println("Invalid JSON or error parsing: ${e.localizedMessage}")
                send("""{"from":"pos","type":"error","data":"Invalid payload"}""")
            }
        }

        override fun onPong(pong: WebSocketFrame?) {}
        override fun onException(exception: IOException) {
            println("WebSocket exception: ${exception.message}")
        }
    }
}
