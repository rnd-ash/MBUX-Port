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
        var log: String = ""
        fun processKombiResponse(f: CarCanFrame) {
            if (f.data[0] != 0x30.toByte()) {
                val size = f.data[0].toInt()
                println("Kombi ISO Frame: ${f.data.drop(1).take(size).map { String.format("%02X", it) }.joinToString(" ")}")
            } else {
                println("Kombi Flow control $f")
                ISO15765Protocol.processRespFrame(f)
            }
        }

        fun beginInitSequence() {
            sendBytes(0x05, 0x24, 0x02, 0x60, 0x01, 0x04, 0x00, 0x00, 0x00, 0x15, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05, 0x00, 0x54, 0x45, 0x4C, 0x20, 0x20, 0x00, 0xC7.toByte())
            while(ISO15765Protocol.sendComplete() == ISO15765Protocol.TxState.NOT_SENT) {}
            //sendBytes(0x03, 0x20, 0x02, 0x11, 0xC3.toByte())

            /*
            Thread.sleep(1)
            sendBytes(0x05, 0x20, 0x02, 0x11, 0xC1.toByte())
            Thread.sleep(1)
            sendBytes(0x05, 0x04, 0x06)
            Thread.sleep(1)
            sendBytes(0x03, 0x21, 0x06)
            Thread.sleep(1)
            sendBytes(0x05, 0x21, 0x06)
            Thread.sleep(1)
            sendBytes(0x05, 0x24, 0x02, 0x60, 0x01, 0x04, 0x00, 0x00, 0x00, 0x15, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05, 0x00, 0x54, 0x45, 0x4C, 0x20, 0x20, 0x00, 0xC7.toByte())
            Thread.sleep(2)
            sendBytes(0x03, 0x20, 0x02, 0x11, 0xC3.toByte())
            Thread.sleep(1)
            sendBytes(0x05, 0x20, 0x02, 0x11, 0xC1.toByte())
            Thread.sleep(1)
            sendBytes(0x05, 0x25, 0x06)
            Thread.sleep(1)
            sendBytes(0x03, 0x21, 0x06)
            Thread.sleep(1)
            sendBytes(0x05, 0x21, 0x06)
            Thread.sleep(1)
            sendBytes(0x03, 0x24, 0x02, 0x60, 0x01, 0x01, 0x00, 0x00, 0x00, 0x13, 0x00, 0x01, 0x00, 0x02, 0x00, 0x04, 0x00, 0x20, 0x20, 0x20, 0x20, 0x00, 0xF3.toByte())
            Thread.sleep(5)
            sendBytes(0x03, 0x25, 0x06)
            Thread.sleep(1)
            sendBytes(0x05, 0x24, 0x02, 0x60, 0x01, 0x04, 0x00, 0x00, 0x00, 0x15, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05, 0x00, 0x54, 0x45, 0x4C, 0x20, 0x20, 0x00, 0xC7.toByte())
            Thread.sleep(5)
            sendBytes(0x05, 0x25, 0x06)
            Thread.sleep(1)
            sendBytes(0x03, 0x26, 0x06, 0x01, 0x00, 0x01, 0x0B, 0x10, 0x41, 0x55, 0x44, 0x49, 0x4F, 0x20, 0x4F, 0x46, 0x46, 0x00, 0xC4.toByte())
            Thread.sleep(5)
            sendBytes(0x05, 0x26, 0x01, 0x00, 0x04, 0x04, 0x10, 0x4E, 0x4F, 0x07, 0x10, 0x50, 0x48, 0x4F, 0x4E, 0x45, 0x02, 0x10, 0x02, 0x10, 0x00, 0x97.toByte())
             */
        }

        private fun sendBytes(vararg byte: Byte) {
            ISO15765Protocol.sendBuffer(byte.toTypedArray(), 0x01A4, 0x01D0)
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
