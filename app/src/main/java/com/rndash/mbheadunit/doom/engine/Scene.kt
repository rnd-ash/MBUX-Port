package com.rndash.mbheadunit.doom.engine

import android.opengl.GLES20
import com.rndash.mbheadunit.doom.wad.Int16
import com.rndash.mbheadunit.doom.wad.Int32
import com.rndash.mbheadunit.doom.wad.WadFile
import java.nio.IntBuffer
import android.opengl.GLES30.*
import java.nio.FloatBuffer

@ExperimentalUnsignedTypes
class Scene {
    val loadedTextures = HashMap<String, Int32>()
    val meshes = HashMap<Int32, Array<Mesh>>()

    fun cacheTexture(w: WadFile, name: String) {
        if (loadedTextures[name] == null) {
            loadedTextures[name] = loadTexture(w, name)
        }
    }

    fun newMesh(w: WadFile, texture: String, lightLevel: Int16, vertexes: Array<Point3>): Mesh {
        val vao = IntBuffer.allocate(1)
        glGenVertexArrays(1, vao)
        glBindVertexArray(vao.get(0))

        val vbo = IntBuffer.allocate(1)
        glGenBuffers(1, vbo)
        glBindBuffer(GL_ARRAY_BUFFER, vbo.get(0))

        val vboData = FloatBuffer.wrap(ArrayList<Float>().apply {
            vertexes.forEach {
                this.add(it.X.toFloat())
                this.add(it.Y.toFloat())
                this.add(it.Z.toFloat())
                this.add(it.U)
                this.add(it.V)
            }
        }.toFloatArray())

        glBufferData(GL_ARRAY_BUFFER, vboData.capacity()*4, vboData, GL_STATIC_DRAW)

        val vtx: Int32 = 0
        glVertexAttribPointer(vtx, 3, GL_FLOAT, false, 5*4, 0)
        glEnableVertexAttribArray(vtx)

        val tex = 1
        glVertexAttribPointer(tex, 2, GL_FLOAT, false, 5*4, 3*4)
        glEnableVertexAttribArray(tex)

        return Mesh(texture, vao.get(0), vbo.get(0), vboData.capacity(), (lightLevel.toFloat() / 255.0F))
    }

    private fun loadTexture(w: WadFile, name: String): Int32 {
        val text = w.textures[name] ?: return 0

        val header = text.header
        println("DOOM - Loading texture ${header.name} ${header.width} x ${header.height} - ${header.numPatches} patches required")
        val bytes = UByteArray(header.width * header.height * 4){0x00.toUByte()} // 0x00 so alpha is off
        val texture = Texture(header.width.toInt(), header.height.toInt())
        text.patches.forEach { p ->
            println(p)
            val patch = w.patches[w.patchNames[p.patchNumber.toInt()]] ?: throw Exception("Patch $p not found")
            (0 until header.height).forEach { y ->
                (0 until header.width).forEach { x ->
                    val pixel = patch.pixels[y*header.width+x].toInt()
                    val alpha = when(pixel) {
                        0xFF -> 0
                        else -> 255
                    }
                    val color = w.colourPalettes.data[0].getColour(pixel)
                    texture.setPixel(
                            patch.xOffset.toInt() + x,
                            patch.yOffset.toInt() + y,
                            color.r.toByte(),
                            color.g.toByte(),
                            color.b.toByte(),
                            alpha.toByte()
                    )
                }
            }
        }
        val texId = IntBuffer.allocate(1)
        glGenTextures(1, texId)
        glActiveTexture(GL_TEXTURE0)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                header.width.toInt(),
                header.height.toInt(),
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                texture.toByteBuffer()
        )
        return texId[0]
    }
}