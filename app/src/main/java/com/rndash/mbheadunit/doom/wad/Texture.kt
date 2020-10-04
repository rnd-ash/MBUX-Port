package com.rndash.mbheadunit.doom.wad

import com.rndash.mbheadunit.doom.renderer.ColourMap
import java.lang.Integer.min
import java.nio.ByteBuffer

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
    val rgba = ByteBuffer
            .allocateDirect(4 * header.width * header.height)
            .asIntBuffer()// Transparent by default

    fun cacheTexture(w: WadFile, cp: Array<ColourMap>) {
        val patches = ArrayList<Patch>()
        p.forEach {
            try {
                patches.add(w.readPatch(w.patchNames[it.pNameNumber]))
            } catch (e: Exception) {
                System.err.println("WARNING. Missing patch $it for texture ${header.name}")
            }
        }
        patches.forEachIndexed { index, patch ->
            (0 until patch.height).forEach yLoop@{ y -> // Each row
                (0 until patch.width).forEach xLoop@{ x ->
                    val pixel = patch.pixels[y * patch.width + x]
                    var rgb = cp[p[index].cMap].getRgb(pixel.toInt() and 0xFF)
                    if (pixel == 0xFF.toByte()) {
                        rgb = rgb and 0xFFFFFF00.toInt() // set alpha to 0
                    }
                    val xPos = p[index].xOffset+x
                    val yPos = p[index].yOffset+y
                    if (xPos in 0 until header.width && yPos in 0 until header.height) {
                        rgba.put((yPos * header.width) + xPos, rgb)
                    }
                }
            }
        }
    }
}