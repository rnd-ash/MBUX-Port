package com.rndash.mbheadunit.nativeCan

import com.rndash.mbheadunit.CanFrame

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
    external fun getSendFrame() : ByteArray


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

    private external fun getECUParam(ecuAddr: Int, bus_id: Char, offset: Int, len: Int) : Int

    fun getBFrame(ecuAddr: CanBAddrs) : CanFrame? = getECUFrame(ecuAddr.addr, 'B')
    fun getCFrame(ecuAddr: CanCAddrs) : CanFrame? = getECUFrame(ecuAddr.addr, 'C')

    private fun getECUFrame(ecuAddr: Int, bus_id: Char) : CanFrame? {
        return try {
            val data = getNativeFrame(ecuAddr, bus_id)
            val id = (data[0].toInt() shl 8) or data[1].toInt()
            CanFrame(id, data.drop(2).toByteArray())
        } catch (e: Exception) {
            null
        }
    }

    private external fun getNativeFrame(ecuAddr: Int, bus_id: Char) : ByteArray
}