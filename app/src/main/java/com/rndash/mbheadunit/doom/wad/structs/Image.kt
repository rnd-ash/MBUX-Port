package com.rndash.mbheadunit.doom.wad.structs

import com.rndash.mbheadunit.doom.wad.Int16
import com.rndash.mbheadunit.doom.wad.Int32
import com.rndash.mbheadunit.doom.wad.UInt16


@ExperimentalUnsignedTypes
class Texture(val heaader: TextureHeader, val patches: Array<Patch>): Struct

@ExperimentalUnsignedTypes
class TextureHeader(
        val name: String,
        val masked: Int32,
        val width: Int32,
        val height: Int32,
        val columnDir: Int32,
        val numPatches: UInt16
): Struct


@ExperimentalUnsignedTypes
data class Patch(
        val xOffset: Int32,
        val yOffset: Int32,
        val patchNumber: Int32,
        val stepDir: Int32,
        val colourMap: Int32) : Struct {}

@ExperimentalUnsignedTypes
data class Image constructor(val width: Int32, val height: Int32, val pixels: UByteArray) : Struct{
    /**
     * Creates a RGBA bytearray texture
     */
    fun toByteStream(palette: Palette): ByteArray {
        val bytes = ByteArray(width*height*4){0xFF.toByte()}
        var index = 0
        pixels.forEach {
            if (it == 0xFF.toUByte()) {
                bytes[index + 3] = 0 // Transparent
            } else {
                bytes[index + 0] = palette.red[it.toInt()].toByte()
                bytes[index + 1] = palette.green[it.toInt()].toByte()
                bytes[index + 2] = palette.blue[it.toInt()].toByte()
                // Alpha stays as 0xFF
            }
            index += 4
        }
        return bytes
    }
}