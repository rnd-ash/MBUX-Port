package com.rndash.mbheadunit.doom

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Renderer(): GLSurfaceView.Renderer {

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        glClearColor(0.3F, 0.3F, 0.3F, 1.0F)
    }

    override fun onDrawFrame(unused: GL10) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }
}