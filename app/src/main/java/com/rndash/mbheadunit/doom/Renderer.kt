package com.rndash.mbheadunit.doom

import android.graphics.Bitmap
import android.opengl.GLES20.*
import android.opengl.GLUtils
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.wad.Patch
import com.rndash.mbheadunit.doom.wad.WadFile
import java.nio.ByteBuffer

object Renderer {
    const val vertexShader = """
        uniform mat4 u_MVPMatrix;
        attribute vec4 a_Position;
        attribute vec2 a_texcoords;
        varying vec2 v_texcoords;
        
        void main() {
            v_texcoords = a_texcoords;
            gl_Position = u_MVPMatrix * a_Position;
        }
    """

    const val fragmentShader = """
        precision mediump float; 

        uniform sampler2D u_sampler;
        varying vec2 v_texcoords;
        
        void main() {
            gl_FragColor = texture2D(u_sampler, v_texcoords);
        }
    """


    fun loadShader(str: String, type: Int): Int {
        var shaderHandle = glCreateShader(type)
        if (shaderHandle != 0) {
            glShaderSource(shaderHandle, str)
            glCompileShader(shaderHandle)

            val compileStatus = IntArray(1)
            glGetShaderiv(shaderHandle, GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                System.err.println(glGetShaderInfoLog(shaderHandle))
                glDeleteShader(shaderHandle)
                shaderHandle = 0
            }
        }
        return shaderHandle
    }

    fun createProgram(): Int {
        var handle = glCreateProgram()
        if (handle != 0) {
            loadShader(vertexShader, GL_VERTEX_SHADER).let {
                if (it != 0) { glAttachShader(handle, it) } else {
                    throw Exception("Error attaching vertex shader")
                }
            }
            loadShader(fragmentShader, GL_FRAGMENT_SHADER).let {
                if (it != 0) { glAttachShader(handle, it) } else {
                    throw Exception("Error attaching fragment shader")
                }
            }

            glBindAttribLocation(handle, 0, "a_Position")
            glBindAttribLocation(handle, 1, "a_Color")
            glLinkProgram(handle)

            val linkStatus = IntArray(1)
            glGetProgramiv(handle, GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                System.err.println(glGetProgramInfoLog(handle))
                glDeleteProgram(handle)
                handle = 0
            }
        }
        return handle
    }

    @ExperimentalUnsignedTypes
    fun loadTexture(name: String, w: WadFile, p: Array<ColourMap>, ignByte: Byte = 0xFF.toByte()): Int {
        val texHandle = IntArray(1)
        glGenTextures(1, texHandle, 0)
        if (texHandle[0] != 0) {
            w.cacheTexture(name)?.let {
                it.cacheTexture(w, p) // Cache the RGB values from colour palette
                val bitmap = Bitmap.createBitmap(it.header.width.toInt(), it.header.height.toInt(), Bitmap.Config.ARGB_8888)
                it.rgba.position(0)
                bitmap.copyPixelsFromBuffer(it.rgba)
                glActiveTexture(GL_TEXTURE0)
                glBindTexture(GL_TEXTURE_2D, texHandle[0])
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
                GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
                bitmap.recycle()
            }
        } else {
            System.err.println("Error caching texture $name")
        }
        return texHandle[0]
    }

    @ExperimentalUnsignedTypes
    fun loadFlat(name: String, w: WadFile, p: Array<ColourMap>, ignByte: Byte = 0xFF.toByte()): Int {
        val texHandle = IntArray(1)
        glGenTextures(1, texHandle, 0)
        if (texHandle[0] != 0) {
            w.readFlat(name).let {
                println("loading flat $name")
                val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
                val rgba = ByteBuffer.allocate(64*64*4).apply { asIntBuffer() }
                for (i in 0 until it.capacity()) {
                    rgba.putInt(p[0].getRgb(it[i].toInt() and 0xFF))
                }
                rgba.position(0)
                bitmap.copyPixelsFromBuffer(rgba)
                glActiveTexture(GL_TEXTURE0)
                glBindTexture(GL_TEXTURE_2D, texHandle[0])
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
                GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
                bitmap.recycle()
            }
        } else {
            System.err.println("Error caching texture $name")
        }
        return texHandle[0]
    }

    @ExperimentalUnsignedTypes
    fun loadPatch(patch: Patch, map: ColourMap, ignByte: Int = 0xFF): Int {
        val pHandle = IntArray(1)
        glGenTextures(1, pHandle, 0)
        if (pHandle[0] != 0) {
            val bitmap = Bitmap.createBitmap(patch.width, patch.height, Bitmap.Config.ARGB_8888)
            val rgba = ByteBuffer.allocate(patch.width*patch.height*4).apply { asIntBuffer() }
            for (row in 0 until patch.height) {
                // I dont know why, but splitting and adding the latter portion first seems to fix
                // odd patch shift
                patch.getRow(row).map { map.getRgb(it.toInt() and 0xFF, ignByte and 0xFF) }.let { l ->
                    val right = l.take(4) // Last 5
                    l.drop(4).forEach {
                        rgba.putInt(it)
                    }
                    right.forEach {
                        rgba.putInt(it)
                    }
                }
            }
            rgba.position(0)
            bitmap.copyPixelsFromBuffer(rgba)
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, pHandle[0])
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        } else {
            System.err.println("Error caching patch ${patch}")
        }
        return pHandle[0]
    }
}