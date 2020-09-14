package com.rndash.mbheadunit.doom.engine

import android.graphics.Bitmap
import com.rndash.mbheadunit.doom.DoomSurfaceView
import com.rndash.mbheadunit.doom.objects.StatusBar
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.structs.Palette
import com.rndash.mbheadunit.doom.wad.structs.PatchImage
import java.lang.Integer.min
import java.nio.ByteBuffer


// Represents our internal game engine frame buffer
// Running at 320 x 200 resolution
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class FrameBuffer(w: WadFile) {
    private val statusBar = StatusBar(w)
    private val bmp = Bitmap.createBitmap(320, 200, Bitmap.Config.ARGB_8888)
    companion object {
        var drawCalls = 0
    }


    // This represents the pixels in DOOM colour palette format
    private val paletteBuffer = ByteBuffer.allocate(320 * 200)

    // Represents ARGB Pixels for the frame buffer
    private val pixelBuffer = ByteArray(320 * 200 * 4)

    /**
     * Converts the palette pixels into ARGB Pixels for the framebuffer
     * @param palette Colour palette to use during conversion
     */
    private fun paletteToPixels(palette: Palette) {
        var index = 0
        paletteBuffer.rewind()
        paletteBuffer.array().forEach {
            val b = palette.getColour(it.toInt() and 0xFF)
            pixelBuffer[index++] = b.r.toByte()
            pixelBuffer[index++] = b.g.toByte()
            pixelBuffer[index++] = b.b.toByte()
            pixelBuffer[index++] = 0xFF.toByte()
        }
    }

    fun drawFrameBuffer() : Bitmap {
        statusBar.render(this)
        paletteToPixels(DoomSurfaceView.wadFile.colourPalettes.data[0])
        bmp.copyPixelsFromBuffer(ByteBuffer.wrap(pixelBuffer))
        return bmp
    }

    /**
     * Sets a specific point on screen to be a colour palette byte
     */
    fun setPixelPalette(x: Int, y: Int, pid: Byte) {
        if (pid == 0xFF.toByte()) { return } // Pixel is transparent!

        // Pixel is off screen
        if (x < 0 || x >= 320) { return }
        if (y < 0 || y >= 200) { return }
        paletteBuffer.put(x*320 + y, pid)
    }

    private fun setPixelRow(row: Int, startPixel: Int, buf: ByteArray) {
        val offset = (row * 320) + startPixel
        val maxPixels = min(320 - startPixel, buf.size)
        var pix: Byte
        paletteBuffer.position(offset)
        (0 until maxPixels).forEach { index ->
            pix = buf[index]
            if (pix != 0xFF.toByte()) {
                paletteBuffer.put(pix)
            } else {
                paletteBuffer.position(offset+index)
            }
        }
    }

    fun placePatch2D(xOffset: Int, yOffset: Int, patch: PatchImage) {
        (0 until patch.height).forEach { y ->
            setPixelRow(y+yOffset, xOffset, patch.getRow(y))
        }
    }
}