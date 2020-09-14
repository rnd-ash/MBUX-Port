package com.rndash.mbheadunit.doom.objects

import com.rndash.mbheadunit.doom.engine.FrameBuffer
import com.rndash.mbheadunit.doom.engine.View2D
import com.rndash.mbheadunit.doom.wad.WadFile

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class StatusBar(wad: WadFile) : Drawable {
    val bgImg = wad.extractPatchByName("STBAR")
    val headImgsB = wad.extractPatchesByName("STFB")
    val headImgsST = wad.extractPatchesByName("STFST")
    val headImgsEVL = wad.extractPatchesByName("STFEVL")
    val headImgsKill = wad.extractPatchesByName("STF").filter { !it.name.startsWith("STFB") }.sortedBy { it.name }

    val viewPort: View2D
    val height: Int
    init {
        viewPort = View2D(bgImg!!.height)
        println(headImgsKill.map { "${it.name} ${it.xOffset} ${it.yOffset}" })
        height = viewPort.viewPortHeight
    }

    var lastDrawTime = 0L

    var index = 0
    override fun render() {
        if (System.currentTimeMillis() - lastDrawTime > 100) {
            bgImg?.let {
                // Want bottom of screen!
                viewPort.drawPatch(0, 0, it)
            }
            if (index == headImgsKill.size) {
                index = 0
            }
            headImgsKill[index].let {
                viewPort.drawPatch(149, 1, it)
            }
            index++
            lastDrawTime = System.currentTimeMillis()
        }
    }

    override fun update() {

    }
}