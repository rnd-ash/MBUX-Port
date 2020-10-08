package com.rndash.mbheadunit.doom

import android.opengl.GLES11.glTexCoordPointer
import android.opengl.GLES20.*
import android.opengl.Matrix
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.renderer.Vector3D
import com.rndash.mbheadunit.doom.wad.WadFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.random.Random

@ExperimentalUnsignedTypes
open class Mesh(
    vararg vecs: Vector3D
) {
    private var numVecs = vecs.size

    init {
        require(numVecs >= 3)
    }

    companion object Tex {
        private val textureHandles = HashMap<String, Int>()

        private fun cacheTexture(name: String, w: WadFile, p: Array<ColourMap>): Int {
            textureHandles[name]?.let { return it }
            val res = Render.loadTexture(name, w, p)
            if (res == 0) {
                System.err.println("Texture for $name not bound!")
            }
            textureHandles[name] = res
            return res
        }

        private val flatHandles = HashMap<String, Int>()
        private fun cacheFlat(name: String, w: WadFile, p: Array<ColourMap>): Int {
            flatHandles[name]?.let { return it }
            val res = Render.loadFlat(name, w, p)
            if (res == 0) {
                System.err.println("Texture for $name not bound!")
            }
            flatHandles[name] = res
            return res
        }
    }

    var lightLevel: Float = 1.0f
    fun setLightLevel(raw: Short) {
        lightLevel = raw / 255.0f
    }

    private var texHandle: Int = 0
    fun cacheTexture(name: String, w: WadFile, p: Array<ColourMap>, ll: Int) {
        texHandle = Tex.cacheTexture(name, w, p)
    }

    private var isFlat = false
    fun cacheFlat(name: String, w: WadFile, p: Array<ColourMap>, ll: Int) {
        texHandle = Tex.cacheFlat(name, w, p)
        isFlat = true
    }

    private var vertexBuffer : FloatBuffer =
        ByteBuffer.allocateDirect(12 * numVecs).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                vecs.forEach { put(it.toFloatArray()) }
                position(0)
            }
        }

    private val mVPMatrix = FloatArray(16)
    val modelMatrix = FloatArray(16)

    init {
        Matrix.setIdentityM(modelMatrix, 0)
    }

    fun rotate(deg: Float) {
        Matrix.rotateM(modelMatrix, 0, deg, 0f, 0f, 1f)
    }

    private var mCubeTexCoordinates: FloatBuffer =
            ByteBuffer.allocateDirect(8 * numVecs).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    vecs.forEach { put(it.toTexFA()) }
                    position(0)
                }
            }

    @ExperimentalStdlibApi
    fun draw(viewMatrix: FloatArray, projMatrix: FloatArray) {

        if (texHandle != 0) {
            //glActiveTexture(GL_TEXTURE0)
            //glUniform1i(DoomGlView.mSamplerHandle, 0)
            glBindTexture(GL_TEXTURE_2D, texHandle)
        }
        vertexBuffer.position(0)
        glVertexAttribPointer(DoomGlView.mPositionHandle, 3, GL_FLOAT, false, 3*4, vertexBuffer)
        glEnableVertexAttribArray(DoomGlView.mPositionHandle)

        mCubeTexCoordinates.position(0)
        glVertexAttribPointer(DoomGlView.mTextureCoordinateHandle, 2, GL_FLOAT, false, 0, mCubeTexCoordinates)
        glEnableVertexAttribArray(DoomGlView.mTextureCoordinateHandle)

        Matrix.multiplyMM(mVPMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, projMatrix, 0, mVPMatrix, 0)

        glUniformMatrix4fv(DoomGlView.mMVPMatrixHandle, 1, false, mVPMatrix, 0)
        glDrawArrays(GL_TRIANGLES, 0, numVecs)

    }
}