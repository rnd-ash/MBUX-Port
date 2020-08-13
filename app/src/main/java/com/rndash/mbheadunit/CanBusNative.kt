package com.rndash.mbheadunit

import java.nio.ByteBuffer

/**
 * Native Canbus wrapper for JNI Code
 */
class CanBusNative {
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
}