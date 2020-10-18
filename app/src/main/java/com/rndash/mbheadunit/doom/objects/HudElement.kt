package com.rndash.mbheadunit.doom.objects

import android.opengl.Matrix
import com.rndash.mbheadunit.doom.Mesh2D
import com.rndash.mbheadunit.doom.renderer.Vector2D

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi

/**
 * HUD Layer element from a patch
 */
open class HudElement(
        val w: Float,
        val h: Float,
        val xoffset: Float,
        val yoffset: Float,
) : Mesh2D(
        Vector2D(0f, h, 0f, 0f),
        Vector2D(0f, 0f, 0f, 1f),
        Vector2D(w, 0f, 1f, 1f),
        Vector2D(w, 0f, 1f, 1f),
        Vector2D(w, h, 1f, 0f),
        Vector2D(0f, h, 0f, 0f)) {

    companion object {
        var orthoMatrix = FloatArray(16)
        fun setScreenDimensions(w: Int, h: Int) {
            Matrix.orthoM(orthoMatrix, 0, 0f, 320f, 0f, 200f, 0f, 1f)
        }
    }

    val mesh = FloatArray(12)

    /**
     * Sets position on screen of the element
     * @param x - Bottom Left corner X (From bottom of screen)
     * @param y - Bottom right corner Y (From bottom of screen)
     */
    fun setPosition(x: Float, y: Float) {
        mesh[0] = x
        mesh[1] = y+h

        mesh[2] = x
        mesh[3] = y

        mesh[4] = x+w
        mesh[5] = y

        mesh[6] = x+w
        mesh[7] = y

        mesh[8] = x+w
        mesh[9] = y+h

        mesh[10] = x
        mesh[11] = y+h

        super.vertexBuffer.position(0)
        super.vertexBuffer.put(mesh)
    }
}