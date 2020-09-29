package com.rndash.mbheadunit.doom.renderer

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLUtils
import com.rndash.mbheadunit.doom.SCREENHEIGHT
import com.rndash.mbheadunit.doom.SCREENWIDTH
import com.rndash.mbheadunit.doom.wad.Patch
import com.rndash.mbheadunit.doom.wad.Texture
import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocateDirect
import java.nio.IntBuffer
import java.util.*
import kotlin.math.*

object Renderer {
    private val bitmap = Bitmap.createBitmap(SCREENWIDTH, SCREENHEIGHT, Bitmap.Config.ARGB_8888)

    fun getFrameBuffer(): Bitmap {
        // Iterate over each screen, starting with the background display, and ending on the foreground
        frameBuffer.rewind()
        (0 until SCREENWIDTH * SCREENHEIGHT).forEach {
            frameBuffer.putInt(palette.getRgb(pixelScreenBuffer[it].toInt() and 0xFF))
        }
        frameBuffer.rewind()
        return bitmap.apply { this.copyPixelsFromBuffer(frameBuffer) }
    }

    /**
     * Contains a list of virtual screens, where each virtual screen represents a virtual layer
     * Layer 1 shows the content which can be displayed on screen within the current frame
     * Buffer layers 1,2,3 and 4 are temporary working areas, where shapes can be swapped
     * between each layer to stack various elements, before the assembled result gets
     * pushed to the screen buffer using [Renderer.copyRect] function
     */
    val pixelScreenBuffer = ByteBuffer.allocateDirect(SCREENWIDTH * SCREENHEIGHT)

    // RGBA Array for screen pixels (320x200 resolution) - DOOM Seems to upscale
    private val frameBuffer = ByteBuffer.allocateDirect(SCREENWIDTH * SCREENHEIGHT * 4)

    private var palette = ColourMap(ByteArray(256*3))

    fun setPalette(p: ColourMap) {
        palette = p
    }

    fun drawLineS(x1: Short, y1: Short, x2: Short, y2: Short, col: Byte) {
        drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), col)
    }

    fun drawLine(x: Int, y: Int, x1: Int, y1: Int, col: Byte) {
        when {
            x1 == x && y1 == y -> drawPixel(x, y, col)
            x1 == x -> {
                val range = if (y < y1) (y..y1) else (y1..y)
                range.forEach { drawPixel(x, it, col) }
            }
            y1 == y -> {
                val range = if (x < x1) (x..x1) else (x1..x)
                range.forEach { drawPixel(it, y, col) }
            }
            else -> {
                var dy = (y1 - y).toDouble()
                var dx = (x1 - x).toDouble()
                val steps = max(abs(dy), abs(dx))
                dy /= steps
                dx /= steps
                var sx = x.toDouble()
                var sy = y.toDouble()
                (0 .. steps.toInt()).forEach { _ ->
                    drawPixel(sx.toInt(), sy.toInt(), col)
                    sx += dx
                    sy += dy
                }
            }
        }
    }

    fun drawPixel(x: Int, y: Int, col: Byte) {
        if (x in 0 until SCREENWIDTH && y in 0 until SCREENHEIGHT) {
            pixelScreenBuffer.put(y * SCREENWIDTH + x, col)
        }
    }

    fun drawVLine(x: Int, y1: Int, y2: Int, top: Byte, mid: Byte, bot: Byte) {
        val y1t = y1.coerceIn(0, SCREENHEIGHT-1)
        val y2t = y2.coerceIn(0, SCREENHEIGHT-1)
        if (y2t == y1t) {
            pixelScreenBuffer.put(y1t* SCREENWIDTH+x, mid)
        } else {
            pixelScreenBuffer.put(y1t * SCREENWIDTH + x, top)
            (y1t+1 until y2t).forEach { y ->
                pixelScreenBuffer.put(y * SCREENWIDTH + x, mid)
            }
            pixelScreenBuffer.put(y2t * SCREENWIDTH + x, bot)
        }
    }

    /**
     * Clears framebuffer
     */
    fun clear() {
        Arrays.fill(pixelScreenBuffer.array(), 0x00)
    }


    fun drawPatch(x: Int, y: Int, p: Patch, ignore: Byte = 0xFF.toByte()) {
        val startX = x + p.leftOffset
        val startY = y + p.topOffset
        var startPos = startY * SCREENWIDTH + startX
        try {
            (0 until p.height).forEach { r ->
                pixelScreenBuffer.position(startPos)
                // Ensure transparent pixels are NOT copied
                (0 until p.width).forEach { pxc ->
                    val px = p.pixels[p.width * r + pxc]
                    if (px != ignore) {
                        pixelScreenBuffer.put(px)
                    } else {
                        pixelScreenBuffer.position(pixelScreenBuffer.position() + 1)
                    }
                }
                startPos += SCREENWIDTH
                if (startPos + SCREENWIDTH > pixelScreenBuffer.capacity()) {
                    return
                }
            }
        } catch (e: IllegalArgumentException) {
            System.err.println("Out of bounds detected! Orig: ($x,$y) - Patch size: (${p.width} by ${p.height})")
        }
    }

    fun drawTexture(x: Int, y: Int, p: Texture, ignore: Byte = 0xFF.toByte()) {
        var startPos = y * SCREENWIDTH + x
        try {
            (0 until p.header.height).forEach { r ->
                pixelScreenBuffer.position(startPos)
                // Ensure transparent pixels are NOT copied
                (0 until p.header.width).forEach { pxc ->
                    val px = p.bytes[p.header.width * r + pxc]
                    if (px != ignore) {
                        pixelScreenBuffer.put(px)
                    } else {
                        pixelScreenBuffer.position(pixelScreenBuffer.position() + 1)
                    }
                }
                startPos += SCREENWIDTH
                if (startPos + SCREENWIDTH > pixelScreenBuffer.capacity()) {
                    return
                }
            }
        } catch (e: IllegalArgumentException) {
            System.err.println("Out of bounds detected! Orig: ($x,$y) - Patch size: (${p.header.width} by ${p.header.height})")
        }
    }

    /*
    fun loadShader(type: Int, code: String): Int {
        return glCreateShader(type).also {
            glShaderSource(it, code)
            glCompileShader(it)
        }
    }

    fun loadTexture(bmp: Bitmap) : Int {
        val texHandle = IntArray(1)
        glGenTextures(1, texHandle, 0)
        if (texHandle[0] != GL_FALSE) {
            glBindTexture(GL_TEXTURE_2D, texHandle[0])
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0)
            bmp.recycle()
        } else {
            throw Exception("Error loading texture!")
        }
        return texHandle[0]
    }

     */
}