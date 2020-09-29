package com.rndash.mbheadunit.doom.wad

import android.graphics.Bitmap
import com.rndash.mbheadunit.doom.renderer.ColourMap
import java.nio.ByteBuffer

class Patch (
    val width: Short,
    val height: Short,
    val leftOffset: Short,
    val topOffset: Short,
    val pixels: ByteArray
){
    init {
        // Sanity check
        require(pixels.size == width * height)
    }

    fun getRow(index: Int): ByteArray {
        return pixels.copyOfRange(width*index, width*index + width)
    }
}

class PicHeader (
        val width: Short,
        val height: Short,
        val leftOffset: Short,
        val topOffset: Short,
)