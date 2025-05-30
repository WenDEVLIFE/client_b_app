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
import java.io.IOException
import fi.iki.elonen.NanoWSD.WebSocketFrame
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

object WebsocketServerPOS {
    private var server: POSWebSocketServer? = null

    fun start(port: Int = 8080) {
        if (server != null) return

        server = POSWebSocketServer(port)
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

    private class POSWebSocketServer(port: Int) : NanoWSD(port) {
        override fun openWebSocket(handshake: NanoHTTPD.IHTTPSession?): WebSocket {
            return POSWebSocket(handshake)
        }
    }

    private class POSWebSocket(handshakeRequest: NanoHTTPD.IHTTPSession?) : NanoWSD.WebSocket(handshakeRequest) {
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

                val response = payload.copy(from = "pos")
                val responseJson = json.encodeToString(response)
                send(responseJson)
            } catch (e: Exception) {
                println("Invalid JSON or error parsing: ${e.localizedMessage}")
                send("""{"from":"pos","type":"error","data":"Invalid payload"}""")
            }
        }

        override fun onPong(pong: WebSocketFrame?) {
            // Optional: handle pongs
        }

        override fun onException(exception: IOException) {
            println("WebSocket exception: ${exception.message}")
        }
    }
}
