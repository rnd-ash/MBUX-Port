package com.rndash.mbheadunit.doom.objects

import android.graphics.Canvas
import com.rndash.mbheadunit.doom.engine.FrameBuffer

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
interface Drawable {
    fun render(fb: FrameBuffer)
    fun update()
}