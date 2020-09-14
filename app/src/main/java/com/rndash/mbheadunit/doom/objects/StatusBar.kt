package com.rndash.mbheadunit.doom.objects

import com.rndash.mbheadunit.doom.engine.FrameBuffer
import com.rndash.mbheadunit.doom.wad.WadFile

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class StatusBar(wad: WadFile) : Drawable {

    val bgImg = wad.extractPatchByName("STBAR")
    val headImgsB = wad.extractPatchesByName("STFB")
    val headImgsST = wad.extractPatchesByName("STFST")
    val headImgsEVL = wad.extractPatchesByName("STFEVL")
    val headImgsKill = wad.extractPatchesByName("STF").filter { !it.name.startsWith("STFB") }.sortedBy { it.name }

    init {
        println(headImgsKill.map { "${it.name} ${it.xOffset} ${it.yOffset}" })
    }

    var lastDrawTime = 0L

    var index = 0
    override fun render(fb: FrameBuffer) {
        if (System.currentTimeMillis() - lastDrawTime > 100) {
            bgImg?.let {
                // Want bottom of screen!
                fb.placePatch2D(0, 200 - it.height, it)
            }
            if (index == headImgsKill.size) {
                index = 0
            }
            headImgsKill[index].let {
                fb.placePatch2D(149, 200 - it.height, it)
            }
            index++
            lastDrawTime = System.currentTimeMillis()
        }
    }

    override fun update() {

    }
}