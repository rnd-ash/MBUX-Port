package com.rndash.mbheadunit.beatsaber

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.KeyEvent.*
import com.rndash.mbheadunit.beatsaber.Block.Companion.NOTE_ACTION_DISTANCE
import com.rndash.mbheadunit.beatsaber.Block.Companion.NOTE_HIDE_DISTANCE
import com.rndash.mbheadunit.beatsaber.Block.Companion.NOTE_SPEED
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.doom.Renderer
import com.rndash.mbheadunit.ui.LightsDisplay
import java.lang.Math.cos
import java.lang.Math.sin
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class GlView(private val ctx: Context) : GLSurfaceView.Renderer {
    companion object {
        var positionHandle: Int = 0
        var colourHandle: Int = 0
        var mvpMatrixHandle: Int = 0
        const val LIGHTING_RESOLUTION_MS = 10
    }

    private var viewMatrix = FloatArray(16)
    private val projMatrix = FloatArray(16)

    private var eyeX = 3.5f
    private var eyeY = 3.5f
    private var eyeZ = 0f // Updated by camera

    private var dirX = 0f
    private var dirY = 0.4f
    private var dirZ = 1f


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 300.0f)
    }

    private var programHandle = 0

    private var isLevelPlaying = false

    fun isLevelPlaying() = isLevelPlaying

    // +y is closer to ground
    // +x is left -x is right
    // +z
    private var blocks = ArrayList<Block>()
    // Processes the input level and returns in processing was OK
    private lateinit var level: Info
    private var numBlocks = blocks.size
    // Lighting for the car
    private var lightingEvents: ArrayList<LightEvent> = arrayListOf()
    fun processChosenLevel(level: Info, levelIdx: Int): Boolean {
        blocks = level.levels[levelIdx].processBlocks(level.bpm)
        if (blocks.size > 0) {
            this.level = level
            numBlocks = blocks.size
            startPos = 0
            lightingEvents = level.levels[levelIdx].processLights(level.bpm, LIGHTING_RESOLUTION_MS)
            posUpdaterThread.start()
            return true
        }
        return false
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Renderer.createProgramBS().let {
            if (it == 0) {
                throw Exception("Error creating OpenGl program!")
            } else {
                programHandle = it
            }
        }
        glUseProgram(programHandle)
        positionHandle = glGetAttribLocation(programHandle, "vPosition")
        colourHandle = glGetAttribLocation(programHandle, "vColour")
        mvpMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix")

        updateCameraPos()

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
    }

    var startPos = 0
    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        glClearColor(0f, 0f, 0f, 0f) // Transparent (Background)

        // 3D rendering goes here:
       for (i in startPos until numBlocks) {
            when(blocks[i].draw(viewMatrix, projMatrix, eyeZ)) {
                Renderable.RenderPosition.BEHIND_CAMERA -> startPos++
                Renderable.RenderPosition.REDERED -> {}
                Renderable.RenderPosition.DISTANT -> break
            }
        }
    }

    fun updateCameraPos() {
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, eyeX+dirX, eyeY+dirY, eyeZ+dirZ, 0f, 1f, 0f)
    }

    val posUpdaterThread = Thread {
        // Start the party mode thread
        PartyMode.startThread()
        level.songFile.start()
        val beatPerMs = level.bpm / 60000f
        var startIdx = 0
        LightsDisplay.lightingEvents = 0
        var pos = 0f
        while(level.songFile.isPlaying) {
            pos = level.songFile.currentPosition.toFloat()
            eyeZ = ((beatPerMs * pos) * NOTE_SPEED) - NOTE_ACTION_DISTANCE
            updateCameraPos()
            val lightTimestamp = (pos / LIGHTING_RESOLUTION_MS).toInt() * LIGHTING_RESOLUTION_MS
            for (i in (startIdx until lightingEvents.size)) {
                // Found light event in time bucket, animate it
                if (lightingEvents[i].timestampMs == lightTimestamp) {
                    lightingEvents[i].animate()
                    // This event is done, move the start index
                    startIdx++
                } else if (lightingEvents[i].timestampMs > lightTimestamp) {
                    // Later on, stop looping
                    break
                }
            }
            Thread.sleep(5)
        }
        // Reset when done
        blocks.clear()
        lightingEvents.clear()
        eyeZ = 0f
    }
}