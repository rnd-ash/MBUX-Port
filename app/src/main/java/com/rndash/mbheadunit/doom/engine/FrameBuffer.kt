package com.rndash.mbheadunit.doom.engine

import android.graphics.Bitmap
import com.rndash.mbheadunit.doom.DoomSurfaceView
import com.rndash.mbheadunit.doom.objects.StatusBar
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.structs.Palette
import com.rndash.mbheadunit.doom.wad.structs.PatchImage
import java.lang.Integer.min
import java.nio.BufferOverflowException
import java.nio.ByteBuffer


// Represents our internal game engine frame buffer
// Running at 320 x 200 resolution
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class FrameBuffer(w: WadFile) {
    private val statusBar = StatusBar(w)
    private val bmp = Bitmap.createBitmap(320, 200, Bitmap.Config.ARGB_8888)

    // This represents the pixels in DOOM colour palette format
    private val paletteBuffer = ByteBuffer.allocate(320 * 200)

    // Represents ARGB Pixels for the frame buffer
    private val pixelBuffer = ByteArray(320 * 200 * 4)

    companion object {
        private var viewPorts : Array<ViewPort?>? = null

        fun setupViewPorts(numPorts: Int) {
            viewPorts = Array(numPorts){null}
        }

        fun addViewPort(vp: ViewPort, index: Int) {
            viewPorts!![index] = vp
        }

        fun removeViewPort(pos: Int) {
            viewPorts!![pos] = null
        }

        fun removeAllViewPorts() {
            viewPorts = null
        }
    }

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
        paletteBuffer.rewind()
        viewPorts?.forEach { port ->
            port?.let {
                try {
                    paletteBuffer.put(it.render())
                } catch (e: BufferOverflowException) {
                    System.err.println("View port overflow!")
                }
            }
        }
        paletteToPixels(DoomSurfaceView.wadFile.colourPalettes.data[0])
        bmp.copyPixelsFromBuffer(ByteBuffer.wrap(pixelBuffer))
        return bmp
    }

}