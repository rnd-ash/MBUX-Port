package com.rndash.mbheadunit.doom.wad

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class DoomFileIO(private val data: ByteArray) {
    var pos = 0
    fun readBytes(len: Int): ByteArray {
        val buf = ByteArray(len)
        (0 until len).forEach { buf[it] = data[it+pos] }
        pos += len
        return buf
    }

    fun readUntil(end: Byte): ByteArray {
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

    fun readInt32(): Int32 {
        val d = readBytes(4)
        return (
                // Little endian
                ((d[3].toInt() and 0xFF) shl 24) or
                ((d[2].toInt() and 0xFF) shl 16) or
                ((d[1].toInt() and 0xFF) shl 8) or
                (d[0].toInt() and 0xFF)
            ).toUInt()
    }

    fun readInt16(): Int16 {
        val d = readBytes(2)
        return (
                // Little endian
                ((d[0].toInt() and 0xFF) shl 8) or
                (d[1].toInt() and 0xFF)
            ).toUShort()
    }

    fun readByte(): Byte {
        return readBytes(1)[0]
    }

    fun readString(): String {
        return String(readUntil(0x00), Charsets.UTF_8)
    }

    /**
     * Dictionary strings on DOOM FS are 8 bytes long, can be smaller, then padded up to the 8th
     * byte
     */
    fun readString(len: Int): String {
        return String(readBytes(len), Charsets.US_ASCII)
    }
}