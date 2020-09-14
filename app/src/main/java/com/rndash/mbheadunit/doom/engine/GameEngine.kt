package com.rndash.mbheadunit.doom.engine

import android.graphics.Bitmap
import com.rndash.mbheadunit.doom.objects.StatusBar
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.DoomSurfaceView.Companion.NATIVE_HEIGHT
import com.rndash.mbheadunit.doom.objects.MapView

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class GameEngine(wad: WadFile) {
    val fb = FrameBuffer(wad)
    val statusBar = StatusBar(wad)
    val worldSpace = MapView(NATIVE_HEIGHT - statusBar.height, wad.levels[0])
    init {
        FrameBuffer.removeAllViewPorts()
        FrameBuffer.setupViewPorts(2)
        FrameBuffer.addViewPort(worldSpace.viewPort, 0)
        FrameBuffer.addViewPort(statusBar.viewPort, 1)
    }

    fun render(): Bitmap {
        worldSpace.render()
        statusBar.render()
        return fb.drawFrameBuffer()
    }
}