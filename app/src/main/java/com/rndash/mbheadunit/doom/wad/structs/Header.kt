package com.rndash.mbheadunit.doom.wad.structs

import com.rndash.mbheadunit.doom.wad.Int32

@ExperimentalUnsignedTypes
class Header(id: ByteArray, val numLumps: Int32, val infoTableOffset: Int32) : Struct {
    val identification = String(id, Charsets.US_ASCII)

    override fun toString(): String {
        return "Header(id=${identification}, numLumps=${numLumps}, infoTableOffset=${infoTableOffset})"
    }
}