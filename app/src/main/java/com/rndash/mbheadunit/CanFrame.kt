package com.rndash.mbheadunit
import java.nio.ByteBuffer
import kotlin.experimental.and

class CanFrame(val canID: Int, val bus: Char, bytes: ByteArray) {

    constructor(canID: Int, bus: Char, size: Int) : this(canID, bus, ByteArray(size))

    private var data : Long = 0
    init {
        require (bytes.size <= 8) { "Too many bytes for frame content! Max size is 8" }
        for (i in bytes.indices) {
            data = data or (bytes[i].toLong() and 0xFF shl 56-(i*8))
        }
    }
    var dlc = bytes.size

    fun setBitRange(offset: Int, len: Int, value: Int) {
        if (offset+len > dlc*8) {
            throw IndexOutOfBoundsException("Offset+Len > ${dlc*8}")
        }
        if (len == 1) {
            data = data and (1L shl 63-offset).inv() // Clear first!
            val x = if (value == 0) 0 else 1L
            data = data or (x shl 63-offset)
        } else {
            // Zero out area to be set
            for (i in 0 until len) {
                data = data and (1 shl 63-(offset+i)).toLong().inv()
            }

            // Set new value
            var mask = 0L
            for (i in 0 until len) {
                mask = (mask or (1 shl i).toLong())
            }
            data = data or ((mask and value.toLong()) shl 64-(offset+len))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) { return false }
        if (other is CanFrame) {
            return other.dlc == this.dlc
                    && other.canID == this.canID
                    && other.data == this.data
        }
        return false
    }

    // Sets all bytes to 0x00
    fun clear() {
        data = 0
    }

    override fun toString(): String {
        var ret = String.format("BUS %c, ID 0x%04X, Data: [", bus, canID)
        for (i in 0 until dlc) {
            ret += String.format("%02X ", (data shr 56-(i*8)) and 0xFF)
        }
        return ret.replaceRange(ret.length-1, ret.length-1, "]")
    }

    fun getBitRange(offset: Int, len: Int) : Int {
        if (offset+len > dlc*8) {
            throw IndexOutOfBoundsException("Offset+Len > ${dlc*8}")
        }
        var mask = 0L
        for (i in 0 until len) {
            mask = (mask or (1 shl i).toLong())
        }
        return ((data shr 64-(offset+len)) and mask).toInt()
    }

    fun toStruct(): ByteArray {
        return (byteArrayOf(
                bus.toByte(),
                (canID).toByte(),
                (canID shr 8).toByte(),
                dlc.toByte(),
                (data shr 56).toByte(),
                (data shr 48).toByte(),
                (data shr 40).toByte(),
                (data shr 32).toByte(),
                (data shr 24).toByte(),
                (data shr 16).toByte(),
                (data shr 8).toByte(),
                (data shr 0).toByte()
        ))
    }
}