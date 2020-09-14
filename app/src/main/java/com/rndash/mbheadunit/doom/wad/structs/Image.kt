package com.rndash.mbheadunit.doom.wad.structs

import com.rndash.mbheadunit.doom.wad.Int16
import com.rndash.mbheadunit.doom.wad.Int32
import com.rndash.mbheadunit.doom.wad.UInt16


@ExperimentalUnsignedTypes
class Texture(val header: TextureHeader, val patches: Array<Patch>): Struct

@ExperimentalUnsignedTypes
data class TextureHeader(
        val name: String,
        val masked: Int32,
        val width: Int16,
        val height: Int16,
        val columnDir: Int32,
        val numPatches: UInt16
): Struct


@ExperimentalUnsignedTypes
data class Patch(
        val xOffset: Int16,
        val yOffset: Int16,
        val patchNumber: Int16,
        val stepDir: Int16,
        val colourMap: Int16) : Struct {}

@ExperimentalUnsignedTypes
class PatchImage(
        val width: Int32,
        val height: Int32,
        val pixels: ByteArray,
        val xOffset: UInt16,
        val yOffset: UInt16,
        val name: String){

    fun getRow(index: Int) : ByteArray {
        val start = index*width
        return pixels.copyOfRange(start, start+width)
    }

}