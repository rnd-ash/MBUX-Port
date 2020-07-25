package com.rndash.mbheadunit.canData.canB.kombiDisplay
import android.util.Log
import com.rndash.mbheadunit.CarCanFrame
import java.util.Collections.max
import kotlin.math.max

/**
 * Class for implementing the raw communication protocol for communication with IC
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class ICDisplay {

    companion object {
        fun processKombiResponse(f: CarCanFrame) {
            if (f.dlc != 8) {
                Log.e("Kombi-Resp", "Kombi frame invalid!")
                return
            }
            when(f.data[0].toInt() and 0xF0) { // Extract Kombi response nibble
                0x00 -> {
                    val size = f.data[0].toInt()
                    val data = f.data.drop(1).take(size).toByteArray()
                    Log.e("KOMBI DATA","${data.joinToString(",") { String.format("%02X", it) }}}")
                }
                else -> {
                    println("Kombi flow control: $f")
                }
            }
        }
    }

    /**
     * Generates the checksum byte needed for multi-frame payloads to cluster
     */
    private fun genChecksum(bytes: ByteArray): Byte {
        var total = 0
        (bytes.indices).forEach { i ->
            total += i + 1 + bytes[i].toInt()
        }
        return ((255 - total) % 256).toByte()
    }

    /**
     * Sends header message to IC Display
     * @param page Page destination for the header text
     * @param fmt Formatting option for the header text
     * @param str Text string to display - NOTE: This is cropped to 12 characters
     *              due to limitations with the way IC handles text
     */
    fun sendHeader(page: ICDefines.Page, fmt: ICDefines.TextFormat, str: String) {
        val text = str.take(max(12, str.length)) // Only up to 12 characters

        var bytes = byteArrayOf(page.byte, 0x29, fmt.byte)
        bytes += text.toByteArray(Charsets.US_ASCII)
        bytes += 0x00 // Null termination string!
        bytes += genChecksum(bytes)
        ISO15765Protocol.sendBuffer(bytes.toTypedArray(), 0x01A4, 0x01D0)
    }

    fun initPage(page: ICDefines.Page, fmt: ICDefines.TextFormat, str: String, symbolUp: ICDefines.AudioSymbol, symbolDown: ICDefines.AudioSymbol) {
        println("PAGE INIT")
        var bytes = byteArrayOf(page.byte, 0x20, 0x02, 0x11, 0xC1.toByte())
        ISO15765Protocol.sendBuffer(bytes.toTypedArray(), 0x01A4, 0x01D0)
        Thread.sleep(5)
        bytes = byteArrayOf(page.byte, 0x21, 0x06)
        ISO15765Protocol.sendBuffer(bytes.toTypedArray(), 0x01A4, 0x01D0)
        Thread.sleep(5)

        val text = str.take(max(12, str.length)) // Only up to 12 characters
        bytes = byteArrayOf(0x03, 0x24, 0x02, 0x60, 0x01, 0x01, 0x02, 0x00, 0x04, 0x00, 0x20, 0x20, 0x20, 0x20, 0x00, 0xF3.toByte())
        ISO15765Protocol.sendBuffer(bytes.toTypedArray(), 0x01A4, 0x01D0)
    }
}
