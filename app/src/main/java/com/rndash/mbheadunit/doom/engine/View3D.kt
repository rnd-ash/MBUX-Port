package com.rndash.mbheadunit.doom.engine

open class View3D(h: Int) : ViewPort(h) {

    fun setPixelColor(x: Int, y: Int, c: Byte) {
        pixelBuffer.put(y * 320 + x, c)
        pixelUpdates++
    }
}