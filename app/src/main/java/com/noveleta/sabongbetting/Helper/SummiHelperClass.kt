package com.noveleta.sabongbetting.Helper;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.RemoteException
import android.widget.Toast
import com.noveleta.sabongbetting.R
import com.sunmi.peripheral.printer.*
import android.util.Log
import android.os.Handler
import android.os.Looper

object SunmiPrinterHelper {

    var NoSunmiPrinter = 0x00000000
    var CheckSunmiPrinter = 0x00000001
    var FoundSunmiPrinter = 0x00000002
    var LostSunmiPrinter = 0x00000003
    lateinit var mContext: Context

    /**
     * sunmiPrinter means checking the printer connection status
     */
    var sunmiPrinter = CheckSunmiPrinter

    /**
     * SunmiPrinterService for API
     */
    
    private var sunmiPrinterService: SunmiPrinterService? = null
    private var onPrinterReady: (() -> Unit)? = null

    fun initSunmiPrinterService(context: Context, onReady: () -> Unit) {
        this.mContext = context
        this.onPrinterReady = onReady
        
          sunmiPrinterService?.let {
          onReady()
          return
          }
          
        try {
            val ret = InnerPrinterManager.getInstance().bindService(
                context.applicationContext,
                innerPrinterCallback
            )
            if (!ret) {
                sunmiPrinter = NoSunmiPrinter
                Toast.makeText(context, "No printer found", Toast.LENGTH_SHORT).show()
                Log.e("SunmiPrinterHelper", "bindService returned false")
            } else {
                Log.d("SunmiPrinterHelper", "bindService called successfully")
            }
        } catch (e: InnerPrinterException) {
            sunmiPrinter = NoSunmiPrinter
            Log.e("SunmiPrinterHelper", "InnerPrinterException: ${e.localizedMessage}")
            Toast.makeText(context, "Printing failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private val innerPrinterCallback: InnerPrinterCallback = object : InnerPrinterCallback() {
        override fun onConnected(service: SunmiPrinterService) {
            sunmiPrinterService = service
            sunmiPrinter = FoundSunmiPrinter
            Log.d("SunmiPrinterHelper", "Printer connected")
            
            Handler(Looper.getMainLooper()).postDelayed({
                if (isPrinterReady()) {
                    Log.d("SunmiPrinterHelper", "Calling onPrinterReady()")
                    Toast.makeText(mContext, "Printer connected", Toast.LENGTH_SHORT).show()
                    onPrinterReady?.invoke()
                } else {
                    Log.e("SunmiPrinterHelper", "Printer service is still null after connection")
                    Toast.makeText(mContext, "Printer not ready after connect", Toast.LENGTH_SHORT).show()
                }
            }, 300) // Delay to ensure service is ready
        }

        override fun onDisconnected() {
            sunmiPrinterService = null
            sunmiPrinter = LostSunmiPrinter
            Log.w("SunmiPrinterHelper", "Printer disconnected")
            Toast.makeText(mContext, "Printer disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    fun isPrinterReady(): Boolean {
        return sunmiPrinterService != null
    }
    
    /**
     * Initialize the printer
     * All style settings will be restored to default
     */
    fun initPrinter() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.printerInit(null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }
    
    fun printLabelValue(label: String, value: String) {
    try {
        val data = formatLabelValue(label, value)
        sunmiPrinterService?.sendRAWData(data, null)
    } catch (e: RemoteException) {
        e.printStackTrace()
    }
    }
    
    /**
     * deInit sunmi print service
     */
    fun deInitSunmiPrinterService(context: Context?) {
        try {
            if (sunmiPrinterService != null) {
                InnerPrinterManager.getInstance().unBindService(context, innerPrinterCallback)
                sunmiPrinterService = null
                sunmiPrinter = LostSunmiPrinter
            }
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    /**
     * Check the printer connection,
     * like some devices do not have a printer but need to be connected to the cash drawer through a print service
     */
    private fun checkSunmiPrinterService(service: SunmiPrinterService) {
        var ret = false
        try {
            ret = InnerPrinterManager.getInstance().hasPrinter(service)
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
        sunmiPrinter = if (ret) FoundSunmiPrinter else NoSunmiPrinter
    }

    /**
     * Some conditions can cause interface calls to fail
     * For example: the version is too low、device does not support
     * You can see [ExceptionConst]
     * So you have to handle these exceptions
     */
    private fun handleRemoteException(e: RemoteException) {
        //TODO process when get one exception
    }

    /**
     * send esc cmd
     */
    fun sendRawData(data: ByteArray?) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.sendRAWData(data, null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }
    
    
    fun formatLabelValue(label: String, value: String): ByteArray {
    val normal = byteArrayOf(0x1B, 0x45, 0x00) // ESC E 0 - disable bold
    val bold = byteArrayOf(0x1B, 0x45, 0x01)   // ESC E 1 - enable bold
    val reset = byteArrayOf(0x1B, 0x21, 0x00)  // ESC ! 0 - reset styles

    val labelBytes = label.toByteArray(Charsets.UTF_8)
    val valueBytes = value.toByteArray(Charsets.UTF_8)

    return normal + labelBytes + bold + valueBytes + reset + "\n".toByteArray()
    }


    

    /**
     * paper feed three lines
     * Not disabled when line spacing is set to 0
     */
    fun print3Line() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.lineWrap(3, null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }

    /**
     * Get printer serial number
     */
    fun getPrinterSerialNo(): String? {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            ""
        } else try {
            sunmiPrinterService!!.printerSerialNo
        } catch (e: RemoteException) {
            handleRemoteException(e)
            ""
        }
    }

    /**
     * Get device model
     */
    fun getDeviceModel(): String? {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            ""
        } else try {
            sunmiPrinterService!!.printerModal
        } catch (e: RemoteException) {
            handleRemoteException(e)
            ""
        }
    }

    /**
     * Get firmware version
     */
    fun getPrinterVersion(): String? {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            ""
        } else try {
            sunmiPrinterService!!.printerVersion
        } catch (e: RemoteException) {
            handleRemoteException(e)
            ""
        }
    }

    /**
     * Get paper specifications
     */
    fun getPrinterPaper(): String? {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            ""
        } else try {
            if (sunmiPrinterService!!.printerPaper == 1) "58mm" else "80mm"
        } catch (e: RemoteException) {
            handleRemoteException(e)
            ""
        }
    }

    /**
     * Get paper specifications
     */
    fun getPrinterHead(callbcak: InnerResultCallback?) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.getPrinterFactory(callbcak)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }

    /**
     * Get printing distance since boot
     * Get printing distance through interface callback since 1.0.8(printerlibrary)
     */
    fun getPrinterDistance(callback: InnerResultCallback?) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.getPrintedLength(callback)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }


    fun setAlign(align: Int) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.setAlignment(align, null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }
    
    /**
     * Printer cuts paper and throws exception on machines without a cutter
     */
    fun cutpaper() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.cutPaper(null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }


    fun feedPaper() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.autoOutPaper(null)
        } catch (e: RemoteException) {
            print3Line()
        }
    }
    
    fun printText(
    content: String?, size: Float = 36f, isBold: Boolean, isUnderLine: Boolean,
    typeface: String?
) {
    if (sunmiPrinterService == null) {
        Toast.makeText(mContext, "Printer not available!", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        sunmiPrinterService!!.setPrinterStyle(
            WoyouConsts.ENABLE_BOLD,
            if (isBold) WoyouConsts.ENABLE else WoyouConsts.DISABLE
        )
        sunmiPrinterService!!.setPrinterStyle(
            WoyouConsts.ENABLE_UNDERLINE,
            if (isUnderLine) WoyouConsts.ENABLE else WoyouConsts.DISABLE
        )

        // Print with large font
        sunmiPrinterService!!.printTextWithFont(content, typeface, size, null)

        // One line spacing after text
        sunmiPrinterService!!.sendRAWData(byteArrayOf(0x0A), null)

        Toast.makeText(mContext, "Print Successful!", Toast.LENGTH_SHORT).show()
    } catch (e: RemoteException) {
        e.printStackTrace()
    }
}

    
    /**
     * print Qr Code
     */
    fun printQr(data: String?, modulesize: Int, errorlevel: Int) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.printQRCode(data, modulesize, errorlevel, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }



    /**
     * print Bar Code
     */
    fun printBarCode(data: String?, symbology: Int, height: Int, width: Int, textposition: Int) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.printBarCode(data, symbology, height, width, textposition, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Print a row of a table
     */
    fun printTable(txts: Array<String?>?, width: IntArray?, align: IntArray?) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.printColumnsString(txts, width, align, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Print pictures and text in the specified orde
     * After the picture is printed,
     * the line feed output needs to be called,
     * otherwise it will be saved in the cache
     * In this example, the image will be printed because the print text content is added
     */
    fun printBitmap(bitmap: Bitmap?, orientation: Int) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            if (orientation == 0) {
                sunmiPrinterService!!.printBitmap(bitmap, null)

            } else {
                sunmiPrinterService!!.printBitmap(bitmap, null)

            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Gets whether the current printer is in black mark mode
     */
    fun isBlackLabelMode(): Boolean {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            false
        } else try {
            sunmiPrinterService!!.printerMode == 1
        } catch (e: RemoteException) {
            false
        }
    }

    /**
     * Gets whether the current printer is in label-printing mode
     */
    fun isLabelMode(): Boolean {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            false
        } else try {
            sunmiPrinterService!!.printerMode == 2
        } catch (e: RemoteException) {
            false
        }
    }
    
    /**
     * Open cash box
     * This method can be used on Sunmi devices with a cash drawer interface
     * If there is no cash box (such as V1、P1) or the call fails, an exception will be thrown
     *
     * Reference to https://docs.sunmi.com/general-function-modules/external-device-debug/cash-box-driver/}
     */
    fun openCashBox() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.openDrawer(null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }

    /**
     * LCD screen control
     * @param flag 1 —— Initialization
     * 2 —— Light up screen
     * 3 —— Extinguish screen
     * 4 —— Clear screen contents
     */
    fun controlLcd(flag: Int) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.sendLCDCommand(flag)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }

    /**
     * Display text SUNMI,font size is 16 and format is fill
     * sendLCDFillString(txt, size, fill, callback)
     * Since the screen pixel height is 40, the font should not exceed 40
     */
    fun sendTextToLcd() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.sendLCDFillString("SUNMI", 16, true, object : InnerLcdCallback() {
                @Throws(RemoteException::class)
                override fun onRunResult(show: Boolean) {
                    //TODO handle result
                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Display two lines and one empty line in the middle
     */
    fun sendTextsToLcd() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            val texts = arrayOf("SUNMI", null, "SUNMI")
            val align = intArrayOf(2, 1, 2)
            sunmiPrinterService!!.sendLCDMultiString(texts, align, object : InnerLcdCallback() {
                @Throws(RemoteException::class)
                override fun onRunResult(show: Boolean) {
                    //TODO handle result
                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Display one 128x40 pixels and opaque picture
     */
    fun sendPicToLcd(pic: Bitmap?) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.sendLCDBitmap(pic, object : InnerLcdCallback() {
                @Throws(RemoteException::class)
                override fun onRunResult(show: Boolean) {
                    //TODO handle result
                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    /**
     * Used to report the real-time query status of the printer, which can be used before each
     * printing
     */
    fun showPrinterStatus(context: Context?) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        var result = "Interface is too low to implement interface"
        try {
            val res = sunmiPrinterService!!.updatePrinterState()
            when (res) {
                1 -> result = "printer is running"
                2 -> result = "printer found but still initializing"
                3 -> result = "printer hardware interface is abnormal and needs to be reprinted"
                4 -> result = "printer is out of paper"
                5 -> result = "printer is overheating"
                6 -> result = "printer's cover is not closed"
                7 -> result = "printer's cutter is abnormal"
                8 -> result = "printer's cutter is normal"
                9 -> result = "not found black mark paper"
                505 -> result = "printer does not exist"
                else -> {}
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
    }
    
}    