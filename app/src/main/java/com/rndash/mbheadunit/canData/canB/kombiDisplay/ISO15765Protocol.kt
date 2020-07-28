package com.rndash.mbheadunit.canData.canB.kombiDisplay

import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.CarComm
import java.lang.Integer.max
import java.lang.Integer.min

/**
 * Implementation of the ISO15765 protocol for sending CAN Frames to a destination ID
 * Built specifically for Headunit <-> IC Display communication
 * @param byteBuffer Buffer of bytes to send on CANBUS
 * @param sendID CAN ID of the sender (Thats the headunit)
 * @param recvID CAN ID of the target ECU to receive status from
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object ISO15765Protocol {
    enum class TxState {
        NOT_SENT,
        TIMEOUT,
        SEND_OK
    }
    fun sendBuffer(byteBuffer: ByteArray, sendID: Int, recvID: Int) {
        sendBuffer(byteBuffer.toTypedArray(), sendID, recvID)
    }
    private var recvID = 0x0000
    private var clearToSend = false
    private var sendComplete = false
    private var timeout = false

    fun processRespFrame(f: CarCanFrame) {
        if (f.canID == recvID) {
            this.clearToSend = true
        }
    }

    fun sendBuffer(byteBuffer: Array<Byte>, sendID: Int, recvID: Int) {
        this.recvID = recvID
        val seperation_time_ms = 0L // Seperation time between sending multi frames
        val totalSize = byteBuffer.size
        var sentBytes = 0
        var startID: Int = 0x10
        sendComplete = false
        clearToSend = false
        timeout = false
        if (byteBuffer.size <= 7) { // Enough bytes to fit into 1 frame, send and forget
            val bytes = Array<Byte>(8){0x00} // Empty 8 byte frame
            bytes[0] = byteBuffer.size.toByte() // Set the nibble for single frame transfer
            // Do something like memcpy in JVM to copy the bytes
            System.arraycopy(byteBuffer, 0, bytes, 1, byteBuffer.size)
            sendFrame(sendID, bytes)
            sendComplete = true
        } else {
            Thread() {
                // We have to send multiple frames! Launch as a thread!
                val bytes = Array<Byte>(8) { 0x00 } // Allocate this now (saves some overhead)
                bytes[0] = 0x10
                bytes[1] = byteBuffer.size.toByte()
                System.arraycopy(byteBuffer, 0, bytes, 2, 6)
                sentBytes = 6
                sendFrame(sendID, bytes)
                println("ISO15765 Await CTS!")
                val time = System.currentTimeMillis()
                while(!clearToSend){
                    Thread.sleep(1)
                    if (System.currentTimeMillis() - time >= 2000) {
                        println("ISO15765 TIMEOUT!")
                        timeout = true
                        return@Thread
                    }
                }
                println("ISO15765 Clear to send!")
                startID = 0x21
                Thread.sleep(10)
                while (sentBytes < totalSize) {
                    bytes[0] = startID.toByte()
                    System.arraycopy(byteBuffer, sentBytes, bytes, 1, min(7, totalSize - sentBytes))
                    sendFrame(sendID, bytes)
                    sentBytes += 7
                    startID++
                    // Reset send ID if it overflows
                    if (startID == 0x30) {
                        startID = 0x20
                    }
                    Thread.sleep(2)
                }
                clearToSend = false
                sendComplete = true
            }.start()
        }
    }

    fun sendComplete() : TxState {
        return when {
            timeout -> TxState.TIMEOUT
            sendComplete -> TxState.SEND_OK
            else -> TxState.NOT_SENT
        }
    }

    private fun sendFrame(id: Int, b: Array<Byte>) {
        CarComm.sendFrame(CarComm.CANBUS_ID.CANBUS_B, CarCanFrame(id, b))
    }
}