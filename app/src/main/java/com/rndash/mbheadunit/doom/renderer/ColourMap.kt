package com.rndash.mbheadunit.doom.renderer

import java.nio.ByteBuffer

class ColourMap(val data: ByteArray) {
    init {
        require(data.size == 256 * 3) { "Data size ${data.size} != 256*3" }
    }
    fun getRgb(index: Int): Int {
        return ((data[index*3+0].toInt() and 0xFF) shl 24) or
                ((data[index*3+1].toInt() and 0xFF) shl 16) or
                ((data[index*3+2].toInt() and 0xFF) shl 8) or
                (data[index*3+3].toInt() and 0xFF)
    }
}