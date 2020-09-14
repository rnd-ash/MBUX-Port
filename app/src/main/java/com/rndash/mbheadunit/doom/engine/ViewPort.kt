package com.rndash.mbheadunit.doom.engine

import java.nio.ByteBuffer

abstract class ViewPort(val viewPortHeight: Int) {
    companion object {
        const val SCREEN_WIDTH = 320
        @Volatile
        var pixelUpdates = 0
    }

    protected val pixelBuffer = ByteBuffer.allocateDirect(viewPortHeight * 320)

    fun render(): ByteBuffer {
        pixelBuffer.rewind()
        return pixelBuffer
    }

}