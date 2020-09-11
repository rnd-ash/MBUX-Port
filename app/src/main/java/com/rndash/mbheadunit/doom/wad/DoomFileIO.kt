package com.rndash.mbheadunit.doom.wad

import java.lang.Integer.min
import java.nio.ByteBuffer
import java.nio.ByteOrder

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class DoomFileIO(private val data: ByteArray) {
    var pos = 0
    fun readBytes(len: Int): ByteArray {
        val maxRead = min(len, data.size-pos)
        val buf = ByteArray(len)
        (0 until maxRead).forEach { buf[it] = data[it+pos] }
        pos += len
        return buf
    }

    private fun readUntil(end: Byte): ByteArray {
        var tmp = pos
        while (tmp < data.size) {
            if (data[tmp] == 0.toByte()) {
                break
            }
            tmp++
        }
        return readBytes(tmp - pos)
    }


    fun seek(loc: Int) {
        if (data.size < loc) {
            throw Exception("Seek EOF!")
        }
        pos = loc
    }

    /**
     * Reads Signed 32bit integer
     */
    fun readInt32() = ByteBuffer.wrap(readBytes(4)).order(ByteOrder.LITTLE_ENDIAN).int

    /**
     * Reads UnSigned 32bit integer
     */
    fun readUInt32() = readInt32().toUInt()

    /**
     * Reads 16 bit signed integer
     */
    fun readInt16() = ByteBuffer.wrap(readBytes(2)).order(ByteOrder.LITTLE_ENDIAN).short

    fun readByte() = readBytes(1)[0]

    fun readString() = String(readUntil(0x00), Charsets.UTF_8)

    /**
     * Dictionary strings on DOOM FS are 8 bytes long, can be smaller, then padded up to the 8th
     * byte
     */
    fun readString(len: Int): String {
        val r = readBytes(len)
        val tmp = ArrayList<Byte>()
        r.forEach {
            if (it != 0x00.toByte()) {
                tmp.add(it)
            } else {
                return@forEach // End of String
            }
        }
        return String(tmp.toByteArray(), Charsets.US_ASCII)
    }
}