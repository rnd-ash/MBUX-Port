package com.rndash.mbheadunit.doom.wad

import java.lang.Integer.min

class TextureHeader(
    val name: String,
    val masked: Int,
    val width: Short,
    val height: Short,
    val cd: Int,
    val numPatches: Short
)

class Texture(
    val header: TextureHeader,
    patches: Array<Patch>
) {
    val bytes = ByteArray(header.width * header.height){0xFF.toByte()} // Transparent by default

    init {
        println("${header.name} Texture size: ${header.width} x ${header.height}")
        patches.forEach { patch ->
            println("${header.name} patch size: ${patch.width} x ${patch.height}")
            println("${header.name} patch offsets: ${patch.leftOffset} x ${patch.topOffset}")
            (0 until patch.height).forEach yLoop@{ y -> // Each row
                (0 until patch.width).forEach xLoop@{ x ->
                    val pixel = patch.pixels[y * patch.width + x]
                    val imgPos = ((patch.leftOffset + x) * patch.height) + (patch.topOffset + y)
                    if (pixel != 0xFF.toByte()) {
                        if (imgPos >= bytes.size) {
                            return@xLoop
                        }
                        bytes[imgPos] = pixel
                    }
                }
            }
        }
    }
}