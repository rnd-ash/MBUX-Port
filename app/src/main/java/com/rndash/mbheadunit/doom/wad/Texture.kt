package com.rndash.mbheadunit.doom.wad

import com.rndash.mbheadunit.doom.renderer.ColourMap
import java.lang.Integer.min
import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocateDirect
import java.nio.IntBuffer

class TextureHeader(
    val name: String,
    val masked: Int,
    val width: Short,
    val height: Short,
    val cd: Int,
    val numPatches: Short
)

class TexturePatch(
        var xOffset: Int,
        var yOffset: Int,
        var pNameNumber: Int,
        var stepDir: Int,
        var cMap: Int,
)

@ExperimentalUnsignedTypes
class Texture(
    val header: TextureHeader,
    private val p: Array<TexturePatch>
) {
    val rgba = IntBuffer.allocate(header.width * header.height)

    fun cacheTexture(w: WadFile, cp: Array<ColourMap>) {
        p.forEach { patch ->
            val image = try {
                w.readPatch(w.patchNames[patch.pNameNumber])
            } catch (e: Exception) {
                System.err.println("Warning. Patch ${w.patchNames[patch.pNameNumber]} not found!")
                return@forEach
            }
            (0 until image.height).forEach yLoop@{ y -> // Each row
                (0 until image.width).forEach xLoop@{ x ->
                    val pixel = image.pixels[y * image.width + x]
                    val rgb = cp[patch.cMap].getRgb(pixel.toInt() and 0xFF)
                    val xPos = patch.xOffset+x
                    val yPos = patch.yOffset+y
                    if (xPos in 0 until header.width && yPos in 0 until header.height) {
                        rgba.put((yPos * header.width) + xPos, rgb)
                    }
                }
            }
        }
    }
}