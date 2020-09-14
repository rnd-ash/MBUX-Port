package com.rndash.mbheadunit.doom.engine

import com.rndash.mbheadunit.doom.wad.structs.PatchImage
import java.lang.Integer.min

@ExperimentalUnsignedTypes
open class View2D(h: Int) : ViewPort(h) {
    init {
        println("2D viewport setup. Height: $h")
    }
    fun drawPatch(xOffset: Int, yOffset: Int, p: PatchImage) {
        if (yOffset > viewPortHeight) { return } // Off screen
        (0 until p.height).forEach { line ->
            if (line+yOffset > viewPortHeight) { return }
            setPixelRow(line + yOffset, xOffset, p.getRow(line))
        }
    }

    private fun setPixelRow(row: Int, startPixel: Int, buf: ByteArray) {
        val offset = (row * SCREEN_WIDTH) + startPixel
        val maxPixels = min(SCREEN_WIDTH - startPixel, buf.size)
        var pix: Byte
        pixelBuffer.position(offset)
        (0 until maxPixels).forEach { index ->
            pix = buf[index]
            if (pix != 0xFF.toByte()) {
                pixelBuffer.put(pix)
                pixelUpdates++
            } else {
                pixelBuffer.position(offset+index)
            }
        }
    }
}