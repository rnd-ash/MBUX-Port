package com.rndash.mbheadunit
import java.nio.ByteBuffer

class CanFrame(val canID: Int, val bus: Char, bytes: ByteArray) {

    constructor(canID: Int, bus: Char, size: Int) : this(canID, bus, ByteArray(size){0x00})

    var data : ByteBuffer
    init {
        require (bytes.size <= 8) { "Too many bytes for frame content! Max size is 8" }
        data = ByteBuffer.allocate(8).put(bytes)
    }
    var dlc = bytes.size

    fun setBitRange(offset: Int, len: Int, value: Int) {
        if (offset+len > dlc*8) {
            throw IndexOutOfBoundsException("Offset+Len > ${dlc*8}")
        }
        var temp = value
        (0 until len).forEach { counter ->
            var byte = data[(offset + counter) / 8].toInt()
            val bit = ((offset + counter) % 8)
            byte = byte or ((temp and 1) shl 7-bit)
            temp = temp shr 1
            data.put((offset + counter) / 8, byte.toByte())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) { return false }
        if (other is CanFrame) {
            return other.dlc == this.dlc
                    && other.canID == this.canID
                    && other.data.array().contentEquals(this.data.array())
        }
        return false
    }

    // Sets all bytes to 0x00
    fun clear() {
        (0 until dlc).forEach { data.put(it, 0x00) }
    }

    override fun toString(): String {
        return String.format("BUS %c, CID 0x%04X, DLC: %d, Data: [%s]", bus, canID, dlc, data.array().take(dlc).map { x -> String.format("%02X", x) }.joinToString(" "))
    }

    fun getBitRange(offset: Int, len: Int) : Int {
        if (offset+len > dlc*8) {
            throw IndexOutOfBoundsException("Offset+Len > ${dlc*8}")
        }
        if (len == 1) { // Boolean shortcut
            return (this.data[offset / 8].toInt() shr (7 - (offset % 8))) and 1
        } else {
            val start = offset / 8
            val end = (offset + len - 1) / 8
            var d : Int = this.data[start].toInt()
            if (start != end) {
                (start..end).forEach { i ->
                    d = d or data[i].toInt()
                    d = d shl 8
                }
            }
            var mask: Int = 0x00
            (0 until len).forEach {
                mask = mask or (1 shl it)
            }
            // Now bit shift so that masking values start at the start of the byte
            return (d shr offset % 8) and mask
        }
    }

    fun toStruct(): ByteArray {
        return (byteArrayOf(
                bus.toByte(),
                (canID).toByte(),
                (canID shr 8).toByte(),
                dlc.toByte()
        ) + this.data.array())
    }
}