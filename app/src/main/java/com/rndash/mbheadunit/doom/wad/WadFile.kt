package com.rndash.mbheadunit.doom.wad

import android.content.Context
import java.io.File

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class WadFile {
    private val io: DoomFileIO

    /**
     * Loads WAD File from ID
     */
    constructor(r: Int, ctx: Context) {
        io = DoomFileIO(ctx.resources.openRawResource(r).readBytes())
    }

    constructor(f: File) {
        io = DoomFileIO(f.readBytes())
    }

    fun readWad() {
        val wad = WAD()
        val header = readHeader()
        println(String(header.magic))
        if (String(header.magic) != "IWAD") {
            throw Exception("WAD File contains bad magic ID (Not IWAD)")
        }
        wad.header = header
        readInfoTables(wad)
    }

    fun readHeader(): Header {
        val h = Header()
        h.read(io)
        return h
    }

    fun readInfoTables(w: WAD) {
        io.seek(w.header.infoTableOffset.toInt())
        val lumps = HashMap<String, Int>()
        val levels = HashMap<String, Int>()
        val lumpInfos = Array<LumpInfo>(w.header.numLumps.toInt()){ LumpInfo() }
        (0 until w.header.numLumps.toInt()).forEach { i ->
            val l = LumpInfo()
            l.read(io)
            println(l.name)
        }
    }
}