package com.rndash.mbheadunit
import java.lang.IndexOutOfBoundsException
import java.lang.Integer.max
import java.lang.NumberFormatException
import java.util.*

class CarCanFrame(val canID: Int, var data: Array<Byte>) {
    var dlc = data.size

    constructor(canID: Int, data: Array<Int>) : this(canID, data.map { it.toByte() }.toTypedArray())

    constructor(canID: Int, data: Array<Boolean>) : this(canID, arrayOf<Byte>(0x00)){
        val constructedData : Array<Byte> = if (data.size % 8 == 0) {
            Array<Byte>(data.size / 8){0x00}
        } else {
            Array<Byte>(data.size / 8 + 1) {0x00}
        }

        constructedData.indices.forEach { index -> // Which index byte are we on?
            var byte = 0
            // Bit shifting
            try {
                // Try to bit shift
                (0 until 8).forEach { bit ->
                    byte = (byte shl 1) or (if (data[(index*8)+(7-bit)]) 1 else 0)
                }
            }
            // Frame wasn't complete. Ignore and leave result
            catch (e: IndexOutOfBoundsException) {
            }


            // Emplace into list
            constructedData[index] = byte.toByte()
        }

        // Manually set the data
        this.dlc = constructedData.size
        this.data = constructedData
    }

    companion object {
        /**
         * Create a CAN Frame from a hex string sent by the arduino, such as
         *  FFFFXXAABBCC...
         *  Where:
         *  FFFF - CAN ID
         *  XX - Byte 1
         *  AA - Byte 2
         *  BB - Byte 3
         *  CC - Byte 4
         *  Maximum of 8 bytes allowed, any more throws an exception
         */
        fun fromHexStr(str: String) : CarCanFrame? {
            if (str.length < 6) { // Not enough data
                return null
            }
            return try {
                val cid = str.take(4).toLong(16).toInt()
                val b = str.drop(4).chunked(2).dropLast(1)
                val bytes = b.map { it.toLong(16).toByte() }
                CarCanFrame(cid, bytes.toTypedArray())
            } catch (e: NumberFormatException) {
                null
            }
        }
    }

    override fun toString(): String {
        return "ID: $canID DLC: $dlc BYTES: [${data.map{it.toUByte()}.joinToString(",")}]"
    }

    /**
     * Returns the can frame as a custom byte array for the Arduino receiving code
     */
    fun toByteArray() : Array<Byte> {
        return (arrayOf(
                this.canID.toByte(),
                (this.canID shr 8 and 0xFF).toByte(),
                this.dlc.toByte()
        ) + this.data)
    }

    fun toBitArray() : Array<Boolean> {
        val array = Array(this.dlc*8){false}
        this.data.forEachIndexed { index, byte ->
            (0 until 8).forEach { bit ->
                array[(index*8)+(7-bit)] = ((byte.toInt() shr bit) and 1) == 1
            }
        }
        return array
    }
}