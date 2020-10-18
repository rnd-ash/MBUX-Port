package com.rndash.mbheadunit.doom.objects

import com.rndash.mbheadunit.doom.*
import com.rndash.mbheadunit.doom.wad.Patch
import com.rndash.mbheadunit.doom.wad.WadFile
import java.lang.Integer.min

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class StatusBar(private val w: WadFile) {
    private val cMap = w.readPalette()[0] // Always
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

    private lateinit var sbar: HudElement

    private lateinit var minusPatch: HudElement

    // Tall Numbers
    private lateinit var tallNums: Array<HudElement>

    private lateinit var shortNums: Array<HudElement>

    private lateinit var tallPercent: HudElement

    private lateinit var armsBg: Mesh2D


    private val faces = Array(ST_NUMPAINFACES) { FaceAnimation() }

    class FaceAnimation() {
        lateinit var straightFaces: Array<HudElement>
        lateinit var turnLeft: HudElement
        lateinit var turnRight: HudElement
        lateinit var pain: HudElement
        lateinit var evilGrin: HudElement
        lateinit var pissed: HudElement
    }
    @ExperimentalStdlibApi
    private lateinit var faceDead: HudElement
    private lateinit var faceGod: HudElement

    private fun patchToHud(p: Patch): HudElement {
        return HudElement(
                p.width.toFloat(),
                p.height.toFloat(),
                p.leftOffset.toFloat(),
                p.topOffset.toFloat()
        ).apply {
            cachePatch(p, cMap, 0x00)
        }
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
        tallPercent.setPosition(90f, 10f)
        tallPercent.draw()
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
        tallPercent.setPosition(221f, 10f)
        tallPercent.draw()
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
        tallNums[drawNum].setPosition(X.toFloat(), 10f)
        tallNums[drawNum].draw()
        //Renderer.drawPatch(X, ST_Y + 2, tallNums[drawNum], 0x00)
    }

    private fun drawSmallNum(num: Int, X: Int, Y: Int) {
        var drawNum = num
        if (num > 9) {
            drawNum = 9
        }
        //Renderer.drawPatch(X, ST_Y + Y, shortNums[drawNum], 0x00)
    }

    private lateinit var headPic: HudElement

    private fun setHead() {
        headPic = faces[0].straightFaces.random().apply {
            setPosition(SCREENWIDTH/2 - w/2 - xoffset/2, 0f)
        }
    }

    private fun drawArmsBG() {
        armsBg.draw()
    }

    @ExperimentalStdlibApi
    fun setup() {
        sbar = patchToHud(w.readPatch("STBAR"))
        (0 until ST_NUMPAINFACES).forEach { i ->
            val straights = ArrayList<HudElement>()
            (0 until ST_NUMSTRAIGHTFACES).forEach { j ->
                straights.add(patchToHud(w.readPatch(String.format("STFST%d%d", i, j)))) // Straight faces
            }
            faces[i].straightFaces = straights.toTypedArray()
            faces[i].turnRight = patchToHud(w.readPatch(String.format("STFTR%d0", i))) // Turn right
            faces[i].turnLeft = patchToHud(w.readPatch(String.format("STFTL%d0", i))) // Turn left
            faces[i].pain = patchToHud(w.readPatch(String.format("STFOUCH%d", i))) // Ouch!
            faces[i].evilGrin = patchToHud(w.readPatch(String.format("STFEVL%d", i)))// Evil grin face!
            faces[i].pissed = patchToHud(w.readPatch(String.format("STFKILL%d", i))) // Pissed off!
        }
        faceGod = patchToHud(w.readPatch("STFGOD0"))
        faceDead = patchToHud(w.readPatch("STFDEAD0"))

        minusPatch = patchToHud(w.readPatch("STTMINUS"))
        tallPercent = patchToHud(w.readPatch("STTPRCNT"))
        shortNums = w.readPatches("STYSNUM").also {
            require(it.size == 10) { "Could not load all STNUM patches" }
        }.map { patchToHud(it) }.toTypedArray()

        tallNums = w.readPatches("STTNUM").also {
            require(it.size == 10) { "Could not load all STNUM patches" }
        }.map { patchToHud(it) }.toTypedArray()
        armsBg = patchToHud(w.readPatch("STARMS")).apply {
            setPosition(104f, 0f) // Always
        }
    }

    var lastHeadTime = System.currentTimeMillis() - 1000
    @ExperimentalStdlibApi
    fun render(ammo: Int, health: Int, armour: Int) {
        sbar.draw()
        if (System.currentTimeMillis() - lastHeadTime >= 500) {
            lastHeadTime = System.currentTimeMillis()
            setHead()
        }
        headPic.draw()
        drawHealth(health)
        drawArmour(armour)
        drawAmmo(ammo)
        drawArmsBG()
    }
}