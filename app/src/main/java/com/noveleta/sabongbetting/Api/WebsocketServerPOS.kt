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

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.Duration // Server still uses java.time.Duration


    /*
    // ------------------------------------
    // 
    // DEDICATED ONLY FOR POS DEVICES TO RECIEVE PRINT
    //
    // ------------------------------------
    */
    
object WebsocketServerPOS {
    private var server: ApplicationEngine? = null

    fun start(port: Int = 8080) {
        if (server != null) return // Already running

        server = embeddedServer(CIO, port = port, host = "0.0.0.0") {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(30)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }

            routing {
                webSocket("/ws") {
                    val json = Json { ignoreUnknownKeys = true }

                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            try {
                                val payload = json.decodeFromString<BarcodePayload>(message)
                                println("Received: ${payload.data} from ${payload.from}")

                                // Echo or respond
                                val response = json.encodeToString(payload.copy(from = "pos"))
                                send(Frame.Text(response))
                            } catch (e: Exception) {
                                println("Invalid JSON: $message")
                            }
                        }
                    }
                }
            }
        }.start(wait = false)
    }

    fun stop() {
        server?.stop()
        server = null
    }

    // Corrected client definition
    val client = HttpClient(CIO) {
        install(WebSockets) // must use install { } even if no config
    }
}
