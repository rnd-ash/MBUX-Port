package com.rndash.mbheadunit.doom.wad

import java.nio.ByteBuffer

class Patch (
    val name: String,
    val width: Int,
    val height: Int,
    val leftOffset: Int,
    val topOffset: Int,
    val pixels: ByteBuffer
){
    init {
        // Sanity check
        require(pixels.capacity() == width * height)
    }

    fun getRow(index: Int): ByteArray {
        return pixels.array().copyOfRange(width*index, width*index + width)
    }
}

class PicHeader (
        val width: Int,
        val height: Int,
        val leftOffset: Int,
        val topOffset: Int,
)