package com.rndash.mbheadunit.beatsaber

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.KeyEvent.*
import com.rndash.mbheadunit.beatsaber.Block.Companion.NOTE_ACTION_DISTANCE
import com.rndash.mbheadunit.beatsaber.Block.Companion.NOTE_SPEED
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.doom.Renderer
import java.lang.Math.cos
import java.lang.Math.sin
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class GlView(private val ctx: Context) : GLSurfaceView.Renderer {
    companion object {
        var positionHandle: Int = 0
        var colourHandle: Int = 0
        var mvpMatrixHandle: Int = 0
        const val LIGHTING_RESOLUTION_MS = 10
    }

    private val viewMatrix = FloatArray(16)
    private val projMatrix = FloatArray(16)
    private val mvpmatrix = FloatArray(16)

    private var eyeX = 3.5f
    private var eyeY = 3.5f
    private var eyeZ = 0f // Updated by camera

    private var posX = 1f
    private var posY = 0f
    private var posZ = 0f

    private var dirX = 0f
    private var dirY = 0.4f
    private var dirZ = 0f


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
    private var blocks = arrayListOf(
        Block(0, 2, -1f, 0f, 0f, 1f, 1f, 1f, Block.Indicator.LEFT),
        Block(0, 3, -1f, 0f, 0f, 0f, 1f, 1f, Block.Indicator.LEFT),
        Block(0, 0, -1f, 0f, 1f, 0f, 0f, 1f, Block.Indicator.LEFT),
        Block(0, 1, -1f, 0f, 1f, 1f, 0f, 1f, Block.Indicator.LEFT),

        Block(1, 0, -0f, 0f, 1f, 0f, 0f, 1f, Block.Indicator.RIGHT),
        Block(1, 1, -0f, 0f, 1f, 1f, 0f, 1f, Block.Indicator.RIGHT),
        Block(1, 2, -0f, 0f, 0f, 1f, 1f, 1f, Block.Indicator.RIGHT),
        Block(1, 3, -0f, 0f, 0f, 0f, 1f, 1f, Block.Indicator.RIGHT),

        Block(2, 0, -1f, 0f, 1f, 0f, 0f, 1f, Block.Indicator.LEFT),
        Block(2, 1, -1f, 0f, 1f, 1f, 0f, 1f, Block.Indicator.LEFT),
        Block(2, 2, -1f, 0f, 0f, 1f, 1f, 1f, Block.Indicator.LEFT),
        Block(2, 3, -1f, 0f, 0f, 0f, 1f, 1f, Block.Indicator.LEFT),

        Block(3, 0, 1f, 0f, 1f, 0f, 0f, 1f, Block.Indicator.RIGHT),
        Block(3, 1, 1f, 0f, 1f, 1f, 0f, 1f, Block.Indicator.RIGHT),
        Block(3, 2, 1f, 0f, 0f, 1f, 1f, 1f, Block.Indicator.RIGHT),
        Block(3, 3, 1f, 0f, 0f, 0f, 1f, 1f, Block.Indicator.RIGHT),
    )

    // Processes the input level and returns in processing was OK
    private lateinit var level: Info
    private var numBlocks = blocks.size
    // Lighting for the car
    private var lightingEvents: ArrayList<LightEvent> = arrayListOf()
    fun processChosenLevel(level: Info, levelIdx: Int): Boolean {
        blocks = level.levels[levelIdx].processBlocks(level.bpm)
        if (blocks.size > 0) {
            this.level = level
            posUpdaterThread.start()
            numBlocks = blocks.size
            lightingEvents = level.levels[levelIdx].processLights(level.bpm, LIGHTING_RESOLUTION_MS)
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

    override fun onDrawFrame(gl: GL10?) {
        Matrix.multiplyMM(mvpmatrix, 0, projMatrix, 0, viewMatrix, 0)

        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        glClearColor(0f, 0f, 0f, 0f) // Transparent (Background)

        // 3D rendering goes here:
       for (i in 0 until numBlocks) {
            blocks[i].draw(viewMatrix, projMatrix, eyeZ)
        }
    }
    private var camAngle = 0f
    fun updateCameraPos() {
        //println("X $eyeX Y $eyeY Z $eyeZ")

        val x = sin(camAngle * Math.PI / 180.0).toFloat()
        val y = cos(camAngle * Math.PI / 180.0).toFloat()

        dirX = x
        dirZ = y
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, eyeX+dirX, eyeY+dirY, eyeZ+dirZ, 0f, 1f, 0f)
        //Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -4f, 0f, 0f, 0f, 0f, 1f, 0f)

    }

    fun onKeyDown(keyEvent: Int): Boolean {
        when(keyEvent) {
            KEYCODE_W -> posY++
            KEYCODE_S -> posY--
            KEYCODE_A -> posX--
            KEYCODE_D -> posX++
            KEYCODE_Z -> posZ--
            KEYCODE_X -> posZ++
            KEYCODE_Q -> camAngle++
            KEYCODE_E -> camAngle--
        }
        return true
    }
    val posUpdaterThread = Thread {
        // Start the party mode thread
        PartyMode.startThread()
        PartyMode.activateDipped(500)
        Thread.sleep(1000)
        level.songFile.start()
        val msPerBeat = 60000.0f / level.bpm.toFloat()
        var startIdx = 0
        while(true) {
            eyeZ = ((level.songFile.currentPosition / msPerBeat) * NOTE_SPEED) - NOTE_ACTION_DISTANCE
            updateCameraPos()
            val lightTimestamp = (level.songFile.currentPosition.toFloat() / LIGHTING_RESOLUTION_MS.toFloat()).toInt() * LIGHTING_RESOLUTION_MS
            for (i in (startIdx until lightingEvents.size)) {
                // Found light event in time bucket, animate it
                if (lightingEvents[i].timestampMs == lightTimestamp) {
                    lightingEvents[i].animate()
                    // This event is done, move the start index
                    startIdx++
                } else if(lightingEvents[i].timestampMs > lightTimestamp) {
                    // Later on, stop looping
                    break
                }
            }

            Thread.sleep(5)
        }
    }
}