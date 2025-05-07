package com.noveleta.sabongbetting.Helper;

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import android.content.Intent
import android.content.pm.PackageManager
import android.app.Activity

// 1. Define your paper‐width options:
enum class PaperWidth(val cols: Int) {
  WIDTH_50(28),
  WIDTH_80(48)
}

// 2. RFCOMM UUID for SPP profile (most ESC/POS printers use this)
private val PRINTER_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
private const val REQUEST_ENABLE_BT = 1
private const val REQUEST_BLUETOOTH_PERMS = 2

suspend fun connectAndPrint(
  context: Context,
  activity: Activity,
  width: PaperWidth,
  text: String
) = withContext(Dispatchers.IO) {
  // 3. Ensure Bluetooth permissions are granted (Android 12+ needs BLUETOOTH_CONNECT)
  ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
    .takeIf { it == PackageManager.PERMISSION_GRANTED }
    ?: throw SecurityException("BLUETOOTH_CONNECT required")
  
  val adapter = BluetoothAdapter.getDefaultAdapter()
    ?: throw IllegalStateException("No Bluetooth on this device")
    
    // before you try to connect:
if (!adapter.isEnabled) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }

    ActivityCompat.requestPermissions(
        activity,
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT // For Android 12+
        ),
        REQUEST_BLUETOOTH_PERMS
    )


  // 4. Find your Sumni printer among bonded devices
  val sumni: BluetoothDevice = adapter.bondedDevices
    .firstOrNull { it.name.contains("Sumni", ignoreCase = true) }
    ?: throw IOException("Sumni printer not paired")

  // 5. Open RFCOMM socket & connect
  val socket: BluetoothSocket =
    sumni.createRfcommSocketToServiceRecord(PRINTER_UUID)
      .also { it.connect() }
  try {
    socket.outputStream.use { out ->
      // 6. Initialize printer (ESC @)
      out.write(byteArrayOf(0x1B, 0x40))

      // 7. Set line width via “characters per line” logic
      //    (you’ll wrap your own lines to width.cols)
      val cols = width.cols

      // 8. OPTIONAL: select code page, line spacing, etc.
      //    e.g. out.write(byteArrayOf(0x1B, 0x21, 0x00))  // font A, normal

      // 9. Send your text, wrapping at 'cols' characters:
      text
        .chunked(cols)
        .forEach { line ->
          out.write(line.toByteArray(Charsets.ISO_8859_1))
          out.write(byteArrayOf(0x0A))  // LF = new line
        }

      // 10. Feed & cut
      out.write(byteArrayOf(0x1B, 0x64, 0x03)) // ESC d 3 = feed 3 lines
      out.write(byteArrayOf(0x1D, 0x56, 0x00)) // GS V 0 = full cut
    }
  } finally {
    socket.close()
  }
}

