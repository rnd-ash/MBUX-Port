package com.rndash.mbheadunit.doom.things
import com.rndash.mbheadunit.doom.DoomGlView
import com.rndash.mbheadunit.doom.GLMap
import com.rndash.mbheadunit.doom.Renderer
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.mapData.Thing
import java.nio.ByteBuffer
import android.opengl.GLES20.*
import java.nio.ByteOrder
import android.opengl.Matrix
import com.rndash.mbheadunit.doom.wad.Patch


@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
/**
 * All things in a DOOM map use this
 * This sets up the object as a billboard
 * (Always faces the camera)
 */
abstract class AbstractThing(t: Thing, private val l: GLMap)  {
    var xPos = t.xPos.toFloat()
    var zPos = t.yPos.toFloat()
    var yPos = 0F

    fun getHeight() {
        l.findSector(xPos.toInt(), zPos.toInt())?.let { yPos = it.first.floorHeight.toFloat() }
    }

    //abstract val radius: Float
    //abstract val height: Float
    var isVisible = true

    private val mVPMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16){0f}


    abstract var frames: ArrayList<Pair<Patch, Int>>
    abstract fun genSprites(w: WadFile, name: String, map: ColourMap)
    protected var frameID = 0
    protected var frame_num = 0

    var textureArray = ByteBuffer.allocateDirect(48).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 1f, 0f, 1f, 1f, 0f, 1f))
        }
    }
    var vertexArray = ByteBuffer.allocateDirect(12 * 6).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer()
    }

    fun setPos(x: Float, y: Float) {
        val p = frames[frameID].first
        vertexArray.put(0, -xPos)
        vertexArray.put(1, yPos)
        vertexArray.put(2, zPos)

        vertexArray.put(3, -xPos)
        vertexArray.put(4, yPos+p.height)
        vertexArray.put(5, zPos)

        vertexArray.put(6, -(xPos+p.width))
        vertexArray.put(7, yPos+p.height)
        vertexArray.put(8, zPos)

        vertexArray.put(9, -(xPos+p.width))
        vertexArray.put(10, yPos+p.height)
        vertexArray.put(11, zPos)

        vertexArray.put(12, -(xPos+p.width))
        vertexArray.put(13, yPos)
        vertexArray.put(14, zPos)

        vertexArray.put(15, -xPos)
        vertexArray.put(16, yPos)
        vertexArray.put(17, zPos)
    }

    abstract fun physUpdate()

    fun render(viewMatrix: FloatArray, projMatrix: FloatArray) {
        vertexArray.position(0)
        glVertexAttribPointer(DoomGlView.mPositionHandle, 3, GL_FLOAT, false, 3*4, vertexArray)
        glEnableVertexAttribArray(DoomGlView.mPositionHandle)

        val f = frames[frameID]
        if (f.second != 0) {
            glBindTexture(GL_TEXTURE_2D, f.second)
            textureArray.position(0)
            glVertexAttribPointer(DoomGlView.mTextureCoordinateHandle, 2, GL_FLOAT, false, 0, textureArray)
            glEnableVertexAttribArray(DoomGlView.mTextureCoordinateHandle)
        }

        Matrix.multiplyMM(mVPMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, projMatrix, 0, mVPMatrix, 0)

        glUniformMatrix4fv(DoomGlView.mMVPMatrixHandle, 1, false, mVPMatrix, 0)
        glDrawArrays(GL_TRIANGLES, 0, 6)
    }

    fun isTouching(x: Float, y: Float): Boolean {
        //return if
        return false
    }

}