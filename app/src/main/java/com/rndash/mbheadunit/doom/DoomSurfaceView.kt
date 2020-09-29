package com.rndash.mbheadunit.doom

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.doom.objects.Player
import com.rndash.mbheadunit.doom.objects.Scene
import com.rndash.mbheadunit.doom.objects.StatusBar
import com.rndash.mbheadunit.doom.renderer.Renderer
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.nativeCan.canC.SBW_232h
import com.rndash.mbheadunit.nativeCan.canC.SID_SBW
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class DoomSurfaceView(context: Context, private val stretchScreen: Boolean = false) : View(context) {
    private val ANIMATION_DELAY_MS = 1000 / 35 // 35 FPS

    val renderHandler = Handler()
    val runner = object: Runnable {
        override fun run() {
            invalidate()
            renderHandler.postDelayed(this, ANIMATION_DELAY_MS.toLong())
        }
    }

    companion object {
        val inputThread = Thread() {
            var shooting = false
            while (true) {
                if (SBW_232h.get_sid_sbw() != SID_SBW.EWM) {
                    if (!shooting) {
                        PartyMode.activateHazards(50)
                        shooting = true
                    }
                } else {
                    shooting = false
                }
                Thread.sleep(25)
            }
        }
    }

    val w = WadFile(R.raw.doom1, context)
    init {
        Renderer.setPalette(w.readPalette()[0])
        w.loadLevels()
        w.readTextures()
        runner.run()
    }

    private var physScreenWidth = 0
    private var physScreenHeight = 0
    private var transformMatrix = Matrix()


    private fun calcSF() {
        val sfw = physScreenWidth.toFloat() / SCREENWIDTH
        val sfh = physScreenHeight.toFloat() / SCREENHEIGHT
        val sf = min(sfw, sfh)
        if (!stretchScreen) {
            // Forced to keep aspect ratio (Crisp image)
            // Translate the image based on the minimum scale factor
            // Then scale in both x and y by the same amount
            transformMatrix.postTranslate(
                    (physScreenWidth - SCREENWIDTH * sf) / 2.0F,
                    (physScreenHeight - SCREENHEIGHT * sf) / 2.0F
            )
            transformMatrix.preScale(sf, sf)
        } else {
            // Stretch the image using a prescale matrix
            transformMatrix.preScale(sfw, sfh)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        physScreenHeight = h
        physScreenWidth = w
        calcSF()
    }

    val p = Paint().apply {
        color = Color.WHITE
        textSize = 20F
    }

    private var scene = Scene(w.getLevel("E1M2"), w)
    private var lastMeasureTime = 0L
    private var frames = 0
    private var fps = 0
    val s = StatusBar(w)
    override fun onDraw(canvas: Canvas) {
        measureTimeMillis {
            Renderer.clear()
            scene.render() // Draw scene as background
            canvas.drawColor(Color.BLACK) // Set background to be black
            s.render() // Draw the statusbar on top
            canvas.drawBitmap(Renderer.getFrameBuffer(), transformMatrix, p)
        }.let {
            canvas.drawText("Frame time: $it ms", 10F, 35F, p)
        }
        canvas.drawText("FPS: $fps", 10F, 15F, p)

        frames++
        StatusBar.ammo ++
        StatusBar.health += 0.1
        if (StatusBar.ammo > 999) {
            StatusBar.ammo = 0
        }
        if (StatusBar.health > 150) {
            StatusBar.health = -50.0
        }
        if (System.currentTimeMillis() - lastMeasureTime >= 1000) {
            fps = frames
            frames = 0
            lastMeasureTime = System.currentTimeMillis()
        }
    }

    fun processKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_W -> scene.player.fwd(10)
            KeyEvent.KEYCODE_S -> scene.player.rev(10)
            KeyEvent.KEYCODE_A -> scene.player.left(10)
            KeyEvent.KEYCODE_D -> scene.player.right(10)
            KeyEvent.KEYCODE_Q -> scene.player.apply { setAngle((getAngleDegrees()-10).toInt()) }
            KeyEvent.KEYCODE_E -> scene.player.apply { setAngle((getAngleDegrees()+10).toInt()) }
        }
        return super.onKeyUp(keyCode, event)
    }
}