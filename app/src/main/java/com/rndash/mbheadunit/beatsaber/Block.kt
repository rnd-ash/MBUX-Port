package com.rndash.mbheadunit.beatsaber

import android.opengl.GLES20.*
import android.opengl.Matrix
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.ui.LightsDisplay
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Stores data about a block in the beatsaber level
 * @param x: X row number in the matrix
 * @param y: Y column number in the matrix
 * @param z: Z row number (more is further back!)
 * @param ang: Rotation angle of the block
 * @param colour: Colour of the block
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Block(x: Int, y: Int, z: Float, ang: Float, r: Float, g: Float, b: Float, a: Float, private var ind: Indicator) : Renderable {
    companion object {
        const val VERTEX_STRIDE = 12
        const val COLOUR_STRIDE = 16
        const val BLOCK_SPACE = 2.5f
        // At this distance, the block will turn yellow, and indicator will turn on
        const val NOTE_ACTION_DISTANCE = 30f
        // At this distance, the block will disappear, and indicator will turn off
        const val NOTE_HIDE_DISTANCE = 20f
        // Divider for block speed towards the camera
        // Lower number -> Slower blocks
        // Higher number -> Faster blocks
        const val NOTE_SPEED = 20f
    }

    enum class Indicator {
        LEFT,
        RIGHT
    }

    private var yPos = y * BLOCK_SPACE
    private var xPos = x * BLOCK_SPACE
    private var zPos = z

    // Colour data
    private var red = r
    private var green = g
    private var blue = b
    private var alpha = a

    private var colourBuffer: FloatBuffer = ByteBuffer.allocateDirect(4*32).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(
                floatArrayOf(
                    r,g,b,a,
                    r,g,b,a,
                    r,g,b,a,
                    r,g,b,a,
                    r,g,b,a,
                    r,g,b,a,
                    r,g,b,a,
                    r,g,b,a,
                )
            )
            position(0)
        }
    }

    fun setColour(r: Float, g: Float, b: Float, a: Float) {
        colourBuffer.position(0)
        for (i in (0 until 8)) { // Only set the block colour and not the arrow colour!
            colourBuffer.put(4*i+0, r)
            colourBuffer.put(4*i+1, g)
            colourBuffer.put(4*i+2, b)
            colourBuffer.put(4*i+3, a)
        }
        this.red = r
        this.green = g
        this.blue = b
        this.alpha = a
    }
    fun getRed() = this.red
    fun getGreen() = this.green
    fun getBlue() = this.blue
    fun getAlpha() = this.alpha

    // 12 bytes per vertex, 4 vertex's per face, 6 faces
    private var vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(12*8).run {

        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(floatArrayOf(
                -1.0f, -1.0f, -1.0f, //
                1.0f, -1.0f, -1.0f,  //
                1.0f,  1.0f, -1.0f,  //
                -1.0f, 1.0f, -1.0f,  //
                -1.0f, -1.0f,  1.0f,
                1.0f, -1.0f,  1.0f,
                1.0f,  1.0f,  1.0f,
                -1.0f,  1.0f,  1.0f,
            ))
            position(0)
        }
    }

    private val modelMatrix = FloatArray(16)

    init {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, xPos, yPos, zPos)
        Matrix.rotateM(modelMatrix, 0, ang, 0f, 0f, 1f)
    }

    // Short buffer (2 bytes). 6 faces x 6 coordinates per face
    private val indexBuffer = ByteBuffer.allocateDirect(6*6).run {
        order(ByteOrder.nativeOrder())
            put(
                byteArrayOf(
                    0, 4, 5, 0, 5, 1,
                    1, 5, 6, 1, 6, 2,
                    2, 6, 7, 2, 7, 3,
                    3, 7, 4, 3, 4, 0,
                    4, 7, 6, 4, 6, 5,
                    3, 0, 1, 3, 1, 2,
                )
            )
            position(0)
    }

    private var isHighlight = false
    private var finalMpvMatrix = FloatArray(16)


    override fun draw(viewMatrix: FloatArray, projectMatrix: FloatArray, camZ: Float): Renderable.RenderPosition {
        val delta = zPos - camZ

        if (!isHighlight && delta < NOTE_ACTION_DISTANCE) {
            isHighlight = true
            this.setColour(1.0f, 1.0f, 0.0f, 1.0f)
            LightsDisplay.lightingEvents++
            if (this.ind == Indicator.RIGHT) { // Turn on right indicator
                PartyMode.activateRightBlinker(0xFF)
            } else { // Turn on the left one
                PartyMode.activateLeftBlinker(0xFF)
            }
        }

        if (delta < NOTE_HIDE_DISTANCE) {
            LightsDisplay.lightingEvents++
            // Turn off the indicator that was turned on
            if (this.ind == Indicator.RIGHT) { // Turn on right indicator
                PartyMode.activateRightBlinker(0x00)
            } else { // Turn on the left one
                PartyMode.activateLeftBlinker(0x00)
            }
            return Renderable.RenderPosition.BEHIND_CAMERA
        }

        if (delta < 300) { // In View distance
            Matrix.setIdentityM(finalMpvMatrix, 0)
            colourBuffer.position(0)
            vertexBuffer.position(0)
            indexBuffer.position(0)

            Matrix.multiplyMM(finalMpvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(finalMpvMatrix, 0, projectMatrix, 0, finalMpvMatrix, 0)

            glEnableVertexAttribArray(GlView.positionHandle)
            glVertexAttribPointer(
                GlView.positionHandle,
                3,
                GL_FLOAT,
                false,
                VERTEX_STRIDE,
                vertexBuffer
            )

            glEnableVertexAttribArray(GlView.colourHandle)
            glVertexAttribPointer(
                GlView.colourHandle,
                4,
                GL_FLOAT,
                false,
                COLOUR_STRIDE,
                colourBuffer
            )

            glUniformMatrix4fv(GlView.mvpMatrixHandle, 1, false, finalMpvMatrix, 0)

            glDrawElements(GL_TRIANGLES, 6 * 6, GL_UNSIGNED_BYTE, indexBuffer)

            glDisableVertexAttribArray(GlView.positionHandle)
            glDisableVertexAttribArray(GlView.colourHandle)
        } else {
            return Renderable.RenderPosition.DISTANT
        }
        return Renderable.RenderPosition.REDERED
    }
}