package com.rndash.mbheadunit.doom

import android.opengl.GLES20.*
import android.opengl.Matrix
import com.rndash.mbheadunit.doom.objects.HudElement
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.renderer.Vector2D
import com.rndash.mbheadunit.doom.wad.Patch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

@ExperimentalUnsignedTypes
open class Mesh2D(
    vararg vecs: Vector2D
) {
    private var numVecs = vecs.size

    init {
        require(numVecs == 6) // Required rectangle
    }

    companion object PatchC {
        private val patchHandles = HashMap<String, Int>()

        private fun cachePatch(p: Patch, map: ColourMap, ignByte: Int = 0xFF): Int {
            patchHandles[p.name]?.let { return it }
            val res = Renderer.loadPatch(p, map, ignByte)
            if (res == 0) {
                System.err.println("Patch for $p not bound!")
            }
            patchHandles[p.name] = res
            return res
        }
    }

    private var patchHandle: Int = 0
    fun cachePatch(p: Patch, map: ColourMap, ignByte: Int = 0xFF) {
        patchHandle = PatchC.cachePatch(p, map, ignByte)
    }

    // Vertex buffer for Hude element - This can change if the element gets moved
    protected var vertexBuffer : FloatBuffer =
        ByteBuffer.allocateDirect(8 * numVecs).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                vecs.forEach { put(it.toFloatArray()) }
                position(0)
            }
        }

    val modelMatrix = FloatArray(16)

    init {
        Matrix.setIdentityM(modelMatrix, 0)
    }

    fun rotate(deg: Float) {
        Matrix.rotateM(modelMatrix, 0, deg, 0f, 0f, 1f)
    }

    // UV coordinates to texture coordinates - These shouldn't change
    private var mCubeTexCoordinates: FloatBuffer =
            ByteBuffer.allocateDirect(8 * numVecs).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    vecs.forEach { put(it.toTexFA()) }
                    position(0)
                }
            }

    @ExperimentalStdlibApi
    fun draw() {
        vertexBuffer.position(0)
        glVertexAttribPointer(DoomGlView.mPositionHandle, 2, GL_FLOAT, false, 2*4, vertexBuffer)
        glEnableVertexAttribArray(DoomGlView.mPositionHandle)

        if (patchHandle != 0) {
            glBindTexture(GL_TEXTURE_2D, patchHandle)
            mCubeTexCoordinates.position(0)
            glVertexAttribPointer(DoomGlView.mTextureCoordinateHandle, 2, GL_FLOAT, false, 0, mCubeTexCoordinates)
            glEnableVertexAttribArray(DoomGlView.mTextureCoordinateHandle)
        }
        glUniformMatrix4fv(DoomGlView.mMVPMatrixHandle, 1, false, HudElement.orthoMatrix, 0)
        glDrawArrays(GL_TRIANGLES, 0, numVecs)

    }
}