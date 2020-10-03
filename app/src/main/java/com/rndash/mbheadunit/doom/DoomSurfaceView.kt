package com.rndash.mbheadunit.doom

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.os.Handler
import android.view.ContextMenu
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.rndash.mbheadunit.BTMusic
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.doom.objects.Player
import com.rndash.mbheadunit.doom.objects.Scene
import com.rndash.mbheadunit.doom.objects.StatusBar
import com.rndash.mbheadunit.doom.renderer.Renderer
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.nativeCan.canC.MRM_238h
import com.rndash.mbheadunit.nativeCan.canC.MS_210h
import com.rndash.mbheadunit.nativeCan.canC.SBW_232h
import com.rndash.mbheadunit.nativeCan.canC.SID_SBW
import java.io.File
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class DoomSurfaceView(context: Context, private val stretchScreen: Boolean = false, private var w: WadFile, lName: String) : View(context) {
    private val ANIMATION_DELAY_MS = 1000 / 35 // 35 FPS

    val renderHandler = Handler()
    val runner = object: Runnable {
        override fun run() {
            invalidate()
            renderHandler.postDelayed(this, ANIMATION_DELAY_MS.toLong())
        }
    }

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
            //scene.player.setSpeed((MS_210h.get_pw() / 100).toDouble())
            //scene.player.forwards(MS_210h.get_pw() != 0)


            val deg = MRM_238h.get_lw() and 0b11111111 % 10
            if (deg != 0) {
                when(MRM_238h.get_lw_vz()) {
                    //true -> scene.player.setAngle((scene.player.getAngleDegrees() + 2).toInt())
                    //false -> scene.player.setAngle((scene.player.getAngleDegrees() - 2).toInt())
                }
            }
            Thread.sleep(25)
        }
    }

    init {
        Renderer.setPalette(w.readPalette()[0])
        runner.run()
        BTMusic.unfocusBT()
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

    private var scene = Scene(w.getLevel(lName), w, context).apply { startMusic() }
    val s = StatusBar(w, scene.player)
    override fun onDraw(canvas: Canvas) {
        measureTimeMillis {
            Renderer.clearAll()
            scene.render() // Draw scene as background
            canvas.drawColor(Color.BLACK) // Set background to be black
            s.render() // Draw the statusbar on top
            canvas.drawBitmap(Renderer.getFrameBuffer(), transformMatrix, p)
        }.let {
            canvas.drawText("Frame time: $it ms", 10F, 35F, p)
        }
        scene.player.run {
            canvas.drawText(
                    String.format("POS: %d %d | DIR: (%.2f, %.2f) | PLANE: (%.2f, %.2f)",
                            getX(), getY(),
                            getXDir(), getYDir(),
                            getXPlane(), getYPlane()
                            )
                    , 10F, 15F, p)
        }
        StatusBar.ammo ++
        StatusBar.health += 0.1
        if (StatusBar.ammo > 999) {
            StatusBar.ammo = 0
        }
        if (StatusBar.health > 150) {
            StatusBar.health = -50.0
        }
    }

    fun processKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_W -> scene.player.forwards(true)
                KeyEvent.KEYCODE_S -> scene.player.backwards(true)
                KeyEvent.KEYCODE_A -> scene.player.left(true)
                KeyEvent.KEYCODE_D -> scene.player.right(true)
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    fun processKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        /*
        if (event.action == KeyEvent.ACTION_UP) {
            when (keyCode) {
                KeyEvent.KEYCODE_W -> scene.player.forwards(false)
                KeyEvent.KEYCODE_S -> scene.player.backwards(false)
                KeyEvent.KEYCODE_A -> scene.player.left(false)
                KeyEvent.KEYCODE_D -> scene.player.right(false)
            }
        }

         */
        return super.onKeyUp(keyCode, event)
    }
}