package com.rndash.mbheadunit.doom

import android.content.Context
import android.graphics.Canvas
import android.opengl.GLSurfaceView
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.doom.wad.WadFile

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class DoomSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: Renderer

    init {
        setEGLContextClientVersion(2)
        renderer = Renderer()
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        val w = WadFile(R.raw.doom1, context)
        w.readWad()
    }
}