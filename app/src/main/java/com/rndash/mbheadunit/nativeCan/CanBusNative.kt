package com.rndash.mbheadunit.nativeCan

import com.rndash.mbheadunit.CanFrame
import com.rndash.mbheadunit.nativeCan.canB.CanBAddrs
import com.rndash.mbheadunit.nativeCan.canC.CanCAddrs

/**
 * Native Canbus wrapper for JNI Code
 */
object CanBusNative {
    /**
     * Sets up the native canbus interface
     */
    external fun init()

    /**
     * Destroys the native canbus interface
     */
    external fun destroy()

    /**
     * Sends a stream of bytes to the native canbus interface that have been read
     * from the Arduino. These are then stitched together in JNI code to create can frames
     * that have been read by the arduino
     */
    external fun sendBytesToBuffer(bytes: ByteArray, numBytes: Int)

    /**
     * Returns a byte array representing a struct from JNI Code to send back to the Arduino,
     * which represents a can frame to send back to car's canbus
     *
     * If return size is 0, then there is nothing to send
     */
    external fun getSendFrame() : ByteArray?


    /**
     * Gets an ECU Parameter from CAN BUS B
     */
    fun getECUParameterB(ecuAddr: CanBAddrs, offset: Int, len: Int) : Int {
        return try {
            getECUParam(ecuAddr.addr, 'B', offset, len)
        }  catch (e: java.lang.Exception) {
            0
        }
    }

    /**
     * Gets an ECU Parameter from CAN BUS C
     */
    fun getECUParameterC(ecuAddr: CanCAddrs, offset: Int, len: Int) : Int {
        return try {
            getECUParam(ecuAddr.addr, 'C', offset, len)
        } catch (e: java.lang.Exception) {
            0
        }
    }

    /**
     * Returns an optional can frame from canbus B
     * @param ecuAddr Address from Canbus B
     */
    fun getBFrame(ecuAddr: CanBAddrs) : CanFrame? = getECUFrame(ecuAddr.addr, 'B')

    /**
     * Returns an optional can frame from canbus C
     * @param ecuAddr Address from Canbus C
     */
    fun getCFrame(ecuAddr: CanCAddrs) : CanFrame? = getECUFrame(ecuAddr.addr, 'C')

    /**
     * Returns a frame from canbus native
     * @param ecuAddr ECU Frame ID
     * @param bus_id Can Bus Identifier
     */
    private fun getECUFrame(ecuAddr: Int, bus_id: Char) : CanFrame? {
        return try {
            getNativeFrame(ecuAddr, bus_id)?.let {
                val id = (it[0].toInt() shl 8) or it[1].toInt()
                return CanFrame(id, bus_id, it.drop(2).toByteArray())
            }
            return null
        } catch (e: Exception) {
            null
        }
    }

    private external fun getECUParam(ecuAddr: Int, bus_id: Char, offset: Int, len: Int) : Int
    private external fun getNativeFrame(ecuAddr: Int, bus_id: Char) : ByteArray?

    fun setFrameParameter(f: CanFrame, offset: Int, len: Int, raw: Int): CanFrame {
        return f.apply { setBitRange(offset, len, raw) }
    }
}