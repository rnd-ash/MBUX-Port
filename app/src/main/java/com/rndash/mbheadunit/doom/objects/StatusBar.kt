package com.rndash.mbheadunit.doom.objects

import android.opengl.GLES20
import android.opengl.GLES20.*
import com.rndash.mbheadunit.doom.SCREENHEIGHT
import com.rndash.mbheadunit.doom.SCREENWIDTH
import com.rndash.mbheadunit.doom.SCREEN_MUL
import com.rndash.mbheadunit.doom.renderer.Renderer
import com.rndash.mbheadunit.doom.wad.Patch
import com.rndash.mbheadunit.doom.wad.WadFile
import java.lang.Integer.max
import java.lang.Integer.min
import java.nio.*
import kotlin.math.sign

@ExperimentalUnsignedTypes
class StatusBar(private val w: WadFile) {
    companion object {
        const val ST_HEIGHT = 32 * SCREEN_MUL
        const val ST_WIDTH = SCREENWIDTH
        const val ST_Y = (SCREENHEIGHT - ST_HEIGHT)
        const val ST_X = 0
        var ammo = 0
        var health = 0.0
        const val ST_NUMPAINFACES = 5
        const val ST_NUMSTRAIGHTFACES = 3
    }

    val sbar = w.readPatch("STBAR")

    val minusPatch = w.readPatch("STTMINUS")

    // Tall Numbers
    val tallNums = w.readPatches("STTNUM").also {
        require(it.size == 10) { "Could not load all STNUM patches" }
    }

    val shortNums = w.readPatches("STYSNUM").also {
        require(it.size == 10) { "Could not load all STNUM patches" }
    }

    val tallPercent = w.readPatch("STTPRCNT")

    val armsBg = w.readPatch("STARMS")

    private val faces = Array(ST_NUMPAINFACES) { FaceAnimation() }

    class FaceAnimation() {
        lateinit var straightFaces: Array<Patch>
        lateinit var turnLeft: Patch
        lateinit var turnRight: Patch
        lateinit var pain: Patch
        lateinit var evilGrin: Patch
        lateinit var pissed: Patch
    }
    private var faceDead: Patch
    private var faceGod: Patch

    init {
        (0 until ST_NUMPAINFACES).forEach { i ->
            val straights = ArrayList<Patch>()
            (0 until ST_NUMSTRAIGHTFACES).forEach { j ->
                straights.add(w.readPatch(String.format("STFST%d%d", i, j))) // Straight faces
            }
            faces[i].straightFaces = straights.toTypedArray()
            faces[i].turnRight = w.readPatch(String.format("STFTR%d0", i)) // Turn right
            faces[i].turnLeft = w.readPatch(String.format("STFTL%d0", i)) // Turn left
            faces[i].pain = w.readPatch(String.format("STFOUCH%d", i)) // Ouch!
            faces[i].evilGrin = w.readPatch(String.format("STFEVL%d", i))// Evil grin face!
            faces[i].pissed = w.readPatch(String.format("STFKILL%d", i)) // Pissed off!
        }
        faceGod = w.readPatch("STFGOD0")
        faceDead = w.readPatch("STFDEAD0")
    }

    /**
     * Percent must be 0-100%
     */
    private fun drawHealth(perc: Int) {
        var drawPercent = min(100, perc) // Cap health at 100
        if (drawPercent < 0) drawPercent = 0
        // one hundered
        if (drawPercent == 100) {
            drawLargeNum(1, 50)
            drawLargeNum(0, 60)
        }
        // 10's digit
        else if (drawPercent >= 10) {
            drawLargeNum(perc / 10, 60)
        }
        // Lowest digit
        drawLargeNum(drawPercent % 10, 74)
        // percent
        Renderer.drawPatch(90, ST_Y + 2, tallPercent, 0x00)
    }

    /**
     * Percent must be 0-100%
     */
    private fun drawArmour(perc: Int) {
        var drawPercent = min(100, perc) // Cap health at 100
        if (drawPercent < 0) drawPercent = 0
        // one hundered
        if (drawPercent == 100) {
            drawLargeNum(1, 179)
            drawLargeNum(0, 189)
        }
        // 10's digit
        else if (drawPercent >= 10) {
            drawLargeNum(perc / 10, 189)
        }
        // Lowest digit
        drawLargeNum(drawPercent % 10, 205)
        // percent
        Renderer.drawPatch(221, ST_Y + 2, tallPercent, 0x00)
    }

    private fun drawAmmo(amount: Int) {
        return when {
            amount >= 1000 -> drawAmmo(999) // Too big!
            amount >= 100 -> {
                drawLargeNum(amount / 100, 1)
                if (ammo % 100 < 10) {
                    // Need to draw a 0 in the 10's position
                    drawLargeNum(0, 16)
                }
                drawAmmo(amount % 100)
            }
            amount >= 10 -> {
                drawLargeNum(amount / 10, 16)
                drawAmmo(amount % 10)
            }
            amount >= 0 -> drawLargeNum(amount % 10, 31)
            else -> {}
        }
    }

    private fun drawLargeNum(num: Int, X: Int) {
        var drawNum = num
        if (num > 9) {
            drawNum = 9
        }
        Renderer.drawPatch(X, ST_Y + 2, tallNums[drawNum], 0x00)
    }

    private fun drawSmallNum(num: Int, X: Int, Y: Int) {
        var drawNum = num
        if (num > 9) {
            drawNum = 9
        }
        Renderer.drawPatch(X, ST_Y + Y, shortNums[drawNum], 0x00)
    }

    private var stopped = false
    fun start() {
        if (!stopped) {
            stop()
        }
        stopped = false
    }

    fun stop() {
        stopped = true
    }

    var headPic: Patch = faceDead
    private fun drawHead() {
        Renderer.drawPatch(SCREENWIDTH/2 - headPic.width/2 - headPic.leftOffset, SCREENHEIGHT-headPic.height+1, headPic, 0x00)
    }

    private var headUpdate = System.currentTimeMillis()
    private var headNumber = 0
    private fun updateHead() {
        headUpdate = System.currentTimeMillis()
        if (health <= 0) {
            headPic = faceDead
        } else {
            val healthNormalized = max(min(100, health.toInt()), 0)

            // calculate pain index. Larger numbers > more damage (Blood on face)
            val painIndex = max(0, ST_NUMPAINFACES - 1 - (healthNormalized / 20))
            headPic = faces[painIndex].straightFaces[headNumber]
            headNumber++
            if (headNumber >= ST_NUMSTRAIGHTFACES) {
                headNumber = 0
            }
        }
    }

    fun render() {
        Renderer.drawPatch(ST_X, ST_Y, sbar)
        Renderer.drawPatch(104, ST_Y, armsBg)
        drawAmmo(ammo)
        drawHealth(health.toInt())
        drawArmour(health.toInt())
        drawHead()
        if (System.currentTimeMillis() - headUpdate > 250) {
            updateHead()
        }
    }
}