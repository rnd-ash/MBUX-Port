package com.rndash.mbheadunit.doom.renderer

import android.graphics.Bitmap
import com.rndash.mbheadunit.doom.SCREENHEIGHT
import com.rndash.mbheadunit.doom.SCREENWIDTH
import com.rndash.mbheadunit.doom.objects.StatusBar.Companion.ST_HEIGHT
import com.rndash.mbheadunit.doom.objects.StatusBar.Companion.ST_Y
import com.rndash.mbheadunit.doom.wad.Patch
import com.rndash.mbheadunit.doom.wad.Texture
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.*

object Renderer {
    private val bitmap = Bitmap.createBitmap(SCREENWIDTH, SCREENHEIGHT, Bitmap.Config.ARGB_8888)
    private const val PIX_LIM = SCREENHEIGHT * SCREENWIDTH - 1

    fun getFrameBuffer(): Bitmap {
        // Iterate over each screen, starting with the background display, and ending on the foreground
        frameBuffer.rewind()
        //for (i in 0..PIX_LIM) {
        //    frameBuffer.putInt(palette.getRgb(pixelScreenBuffer[i].toInt() and 0xFF))
        //}
        frameBuffer.rewind()
        return bitmap.apply { this.copyPixelsFromBuffer(frameBuffer) }
    }

    val pixelScreenBuffer = ByteBuffer.allocateDirect(SCREENWIDTH * SCREENHEIGHT)

    // RGBA Array for screen pixels (320x200 resolution) - DOOM Seems to upscale
    private val frameBuffer = ByteBuffer.allocateDirect(SCREENWIDTH * SCREENHEIGHT * 4)

    private var palette = ColourMap(ByteBuffer.allocate(256*3))

    fun setPalette(p: ColourMap) {
        palette = p
    }

    fun drawLine(x: Int, y: Int, x1: Int, y1: Int, col: Byte) {
        val start: Int
        val end: Int
        when {
            x1 == x && y1 == y -> drawPixel(x, y, col)
            x1 == x -> {
                if (y < y1) {
                    for (i in y..y1) { drawPixel(x, i, col) }
                } else {
                    for (i in y1..y) { drawPixel(x, i, col) }
                }
            }
            y1 == y -> {
                if (x < x1) {
                    for (i in x..x1) { drawPixel(i, y, col) }
                } else {
                    for (i in x1..x) { drawPixel(i, y, col) }
                }
            }
            else -> {
                var dy = (y1 - y).toDouble()
                var dx = (x1 - x).toDouble()
                val steps = max(abs(dy), abs(dx))
                dy /= steps
                dx /= steps
                var sx = x.toDouble()
                var sy = y.toDouble()
                for (i in 0 .. steps.toInt()) {
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
            for (y in y1t+1 until y2t) {
                pixelScreenBuffer.put(y * SCREENWIDTH + x, mid)
            }
            pixelScreenBuffer.put(y2t * SCREENWIDTH + x, bot)
        }
    }

    fun drawFlat(x: Int, y: Int, f: ByteBuffer) {
        (0 until 64).forEach { yt ->
            if (yt+y >= SCREENHEIGHT) {
                return
            }
            val mx = min(64, SCREENWIDTH-x)
            pixelScreenBuffer.position(((y+yt) * SCREENWIDTH) + x)
            pixelScreenBuffer.put(f.array(),64*yt, mx)
        }
    }

    /**
     * Clears framebuffer (3D space only
     */
    fun clearScene() {
        Arrays.fill(pixelScreenBuffer.array(), 0, 320 * ST_Y, 0x00)
    }

    /**
     * Clears the ENTIRE framebuffer
     */
    fun clearAll() {
        Arrays.fill(pixelScreenBuffer.array(), 0x00)
    }

    fun setSky(col: Byte) {
        Arrays.fill(pixelScreenBuffer.array(), 0, 100 * SCREENWIDTH, col)
    }

    fun setFloor(col: Byte) {
        Arrays.fill(pixelScreenBuffer.array(), 100 * SCREENWIDTH, PIX_LIM, col)
    }

    fun drawPatch(x: Int, y: Int, p: Patch, ignore: Byte = 0xFF.toByte()) {
        val startX = x + p.leftOffset
        val startY = y + p.topOffset
        var startPos = startY * SCREENWIDTH + startX
        try {
            for (r in 0 until p.height) {
                pixelScreenBuffer.position(startPos)
                // Ensure transparent pixels are NOT copied
                for (pxc in 0 until p.width) {
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

    /**
     * Since Statusbar shouldn't be drawn every frame,
     * create a method to copy a temp buffer from the statusbar
     * to the bottom of the screen
     */
    @ExperimentalUnsignedTypes
    fun copyStatusBar(b: ByteBuffer) {
        pixelScreenBuffer.position(320 * ST_Y)
        pixelScreenBuffer.put(b.array(), 0 ,320 * ST_HEIGHT)
    }
}