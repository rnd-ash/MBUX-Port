package com.rndash.mbheadunit.doom

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.view.View
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.doom.engine.FrameBuffer
import com.rndash.mbheadunit.doom.engine.GameEngine
import com.rndash.mbheadunit.doom.engine.ViewPort
import com.rndash.mbheadunit.doom.wad.WadFile
import java.nio.ByteBuffer
import kotlin.math.min
import kotlin.random.Random

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi

class DoomSurfaceView(context: Context, stretchScreen: Boolean = false) : View(context) {
    companion object {
        var screenWidth: Int = 0
        var screenHeight: Int = 0
        const val NATIVE_WIDTH = 320
        const val NATIVE_HEIGHT = 200
        //const val ANIMATION_DELAY_MS = 1000 / 30 // 30 FPS
        const val ANIMATION_DELAY_MS = 10 // For now lets see if we can hit 100fps
        lateinit var wadFile: WadFile
        var scaleWidth = 1F
        var scaleHeight = 1F
        var stretch = false // Integer scaling by default
        fun calcSF(w: Int, h: Int) {
            scaleWidth = w.toFloat() / NATIVE_WIDTH.toFloat()
            scaleHeight = h.toFloat() / NATIVE_HEIGHT.toFloat()
            if (!stretch) {
                screenWidth = screenHeight
            }
        }
    }

    var gameEngine: GameEngine
    val renderHandler = Handler()
    val runner = object: Runnable {
        override fun run() {
            invalidate()
            renderHandler.postDelayed(this, ANIMATION_DELAY_MS.toLong())
        }
    }

    init {
        wadFile = WadFile(R.raw.doom1, context)
        wadFile.readWad()
        runner.run()
        stretch = stretchScreen
        gameEngine = GameEngine(wadFile)
    }

    lateinit var virtualFrameBuffer: ByteArray

    lateinit var frameBufferBitmap: Bitmap
    lateinit var frameBufferRect: Rect
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        screenHeight = h
        screenWidth = w
        calcSF(w, h)
        frameBufferRect = Rect(0, 0, screenWidth, screenHeight)
    }

    /**
     * Converts our 320 x 200 frame buffer used internally to something of the tablets
     * native res
     */
    private fun upscaleToFrameBuffer() {
        frameBufferBitmap = Bitmap.createScaledBitmap(gameEngine.render(), screenWidth, screenHeight, true)
    }

    var drawCalls = 0
    val paint = Paint()
    var fps = 0
    var fbDrawCalls = 0
    var lastFPSTime = System.currentTimeMillis()
    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK) // Set background to be black
        // Upscale frame buffer, and display on screen
        upscaleToFrameBuffer()
        canvas.drawBitmap(frameBufferBitmap, null, frameBufferRect, null)
        drawCalls++


        // Do FPS for debugging
        paint.color = Color.WHITE
        paint.textSize = 20F
        canvas.drawText("FPS: $fps", 10F, 15F, paint)
        canvas.drawText("$fbDrawCalls Pixel updates / sec", 10F, 40F, paint)

        if (System.currentTimeMillis() - lastFPSTime > 1000) {
            fbDrawCalls = ViewPort.pixelUpdates
            ViewPort.pixelUpdates = 0
            fps = drawCalls
            drawCalls = 0
            lastFPSTime = System.currentTimeMillis()
        }
    }
}