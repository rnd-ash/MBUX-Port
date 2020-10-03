package com.rndash.mbheadunit.doom.wad

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

@ExperimentalUnsignedTypes
class Texture(
    val header: TextureHeader,
    private val p: Array<String>
) {
    val bytes = ByteBuffer.allocateDirect(header.width * header.height) // Transparent by default

    fun cacheTexture(w: WadFile) {
        val patches = ArrayList<Patch>()
        p.forEach {
            try {
                patches.add(w.readPatch(it))
            } catch (e: Exception) {
                System.err.println("WARNING. Missing patch $it for texture ${header.name}")
            }
        }
        patches.forEach { patch ->
            (0 until patch.height).forEach yLoop@{ y -> // Each row
                (0 until patch.width).forEach xLoop@{ x ->
                    val pixel = patch.pixels[y * patch.width + x]
                    val imgPos = ((patch.leftOffset + x) * patch.height) + (patch.topOffset + y)
                    if (pixel != 0xFF.toByte()) {
                        if (imgPos >= bytes.capacity()) {
                            return@xLoop
                        }
                        bytes.put(imgPos, pixel)
                    }
                }
            }
        }
    }
}