package com.rndash.mbheadunit

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.KeyEvent
import com.rndash.mbheadunit.beatsaber.GlView

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class BeatSaberGLView(ctx: Context, a: AttributeSet) : GLSurfaceView(ctx, a) {
    var renderer: GlView
    init {
        setZOrderOnTop(true)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)
        setEGLContextClientVersion(2)
        renderer = GlView(ctx)
        setRenderer(renderer)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        renderer.onKeyDown(keyCode)
        return super.onKeyDown(keyCode, event)
    }
}