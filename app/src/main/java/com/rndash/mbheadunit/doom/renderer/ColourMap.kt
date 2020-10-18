package com.rndash.mbheadunit.doom.renderer

import java.nio.ByteBuffer

class ColourMap(val data: ByteBuffer) {
    init {
        require(data.capacity() == 256 * 3) { "Data size ${data.capacity()} != 256*3" }
    }
    fun getRgb(index: Int, ign: Int = 0xFF): Int {
        return if (index == ign) {
            0x00000000
        } else {
            ((data[index * 3 + 0].toInt() and 0xFF) shl 24) or
            ((data[index * 3 + 1].toInt() and 0xFF) shl 16) or
            ((data[index * 3 + 2].toInt() and 0xFF) shl 8) or 0xFF
        }
    }
}
