package com.rndash.mbheadunit.doom.engine

import com.rndash.mbheadunit.doom.wad.Int32
import java.lang.IndexOutOfBoundsException
import java.nio.ByteBuffer

class Pixel(val r: Byte, val g: Byte, val b: Byte, val a: Byte)

// RGBA Texture
class Texture(val width: Int32, val height: Int32) {
    private val map = Array(width*height){ Pixel(0,0,0,0) }

    fun setPixel(x: Int32, y: Int32, r: Byte, g: Byte, b: Byte, a: Byte) {
        try {
            map[(width * y) + x] = Pixel(r, g, b, a)
        } catch (e: IndexOutOfBoundsException){}
    }

    fun toByteBuffer(): ByteBuffer {
        val bb = ByteBuffer.allocate(width * height * 4)
        var index = 0
        map.forEach {
            bb.put(index+0, it.r)
            bb.put(index+1, it.g)
            bb.put(index+2, it.b)
            bb.put(index+3, it.a)
            index += 4
        }
        return bb
    }
}