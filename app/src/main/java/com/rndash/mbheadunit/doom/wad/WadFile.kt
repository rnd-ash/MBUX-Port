package com.rndash.mbheadunit.doom.wad

import android.content.Context
import java.io.File
import javax.net.ssl.SSLEngine

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class WadFile {
    private val io: DoomFileIO
    private lateinit var wad: WAD

    /**
     * Loads WAD File from ID
     */
    constructor(r: Int, ctx: Context) {
        io = DoomFileIO(ctx.resources.openRawResource(r).readBytes())
    }

    constructor(f: File) {
        io = DoomFileIO(f.readBytes())
    }

    fun readWad(): WAD {
        wad = WAD()
        val header = readHeader()
        println(String(header.magic))
        if (String(header.magic) != "IWAD") {
            throw Exception("WAD File contains bad magic ID (Not IWAD)")
        }
        wad.header = header
        readInfoTables()
        wad.playPal = readPlayPal()
        wad.transparentPalleteIndex = 255.toByte() // For now
        wad.pNames = readPatchNames()
        wad.patches = readPatchLumps()
        wad.textures = readTextureLumps()
        wad.flats = readFlatLumps()
        return wad
    }

    private fun readHeader(): Header {
        val h = Header()
        h.read(io)
        return h
    }

    private fun readInfoTables() {
        io.seek(wad.header.infoTableOffset.toInt())
        val lumps = HashMap<String, Int>()
        val levels = HashMap<String, Int>()
        val lumpInfos = Array<LumpInfo>(wad.header.numLumps){ LumpInfo() }
        (0 until wad.header.numLumps).forEach { i ->
            val l = LumpInfo()
            l.read(io)
            if (l.name == "THINGS") {
                val levelID = i-1
                val levelLump = lumpInfos[levelID]
                levels[levelLump.name] = levelID
            }
            lumps[l.name] = i
            lumpInfos[i] = l
        }
        wad.levels = levels
        wad.lumps = lumps
        wad.lumpInfos = lumpInfos
    }

    private fun readPlayPal(): PlayPal {
        val playpalLump = wad.lumps.getOrElse("PLAYPAL") { throw Exception("PLAYPAL not found!")}
        val lumpInfo = wad.lumpInfos.getOrElse(playpalLump) { throw Exception("PLAYPAL index not found!") }
        io.seek(lumpInfo.filePos)
        println("Loading palette...")
        val p = PlayPal()
        p.read(io)
        return p
    }

    private fun readPatchNames(): Array<String> {
        val pNamesLump = wad.lumps.getOrElse("PNAMES") { throw Exception("PNAMES not found!")}
        val lumpInfo = wad.lumpInfos.getOrElse(pNamesLump) { throw Exception("PNAMES index not found!") }
        io.seek(lumpInfo.filePos)
        val count = io.readUInt32()
        println("Loading $count patches")
        val names = Array(count.toInt()){io.readString(8)}
        println("Found patches:")
        println(names.joinToString("\n"))
        return names
    }

    private fun readPatchLumps(): HashMap<String, Image> {
        val patches = HashMap<String, Image>()
        for (pName in wad.pNames) {
            val lumpInfo = wad.lumpInfos[wad.lumps[pName] ?: continue]
            io.seek(lumpInfo.filePos)
            val header = PictureHeader()
            header.read(io)
            if (header.width > 4096 || header.height > 4096) {
                continue
            }
            println("Patch $pName is valid patch. Dimensions: ${header.width} x ${header.height}")
            val pixels = ByteArray(header.width*header.height){wad.transparentPalleteIndex}
            (0 until header.width).forEach { i ->
                var rowStart = 0
                while(rowStart != 255) {
                    rowStart = io.readByte().toInt() and 0xFF // Unsigned!
                    if (rowStart == 255) { break }
                    val pixelCount = io.readByte().toInt() and 0xFF // Unsigned!
                    io.readByte() // Dummy value
                    (0 until pixelCount).forEach { j ->
                        val pixel = io.readByte()
                        pixels[(j+rowStart) * i] = pixel
                    }
                    io.readByte() // Dummy
                }
            }


            patches[pName] = Image().apply {
                this.width = header.width.toInt()
                this.height = header.height.toInt()
                this.pixels = pixels
            }
        }
        return patches
    }

    private fun readTextureLumps() : HashMap<String, Texture> {
        val textureLumps = ArrayList<Int32>()
        wad.lumps["TEXTURE1"]?.let {textureLumps.add(it) }
        wad.lumps["TEXTURE2"]?.let {textureLumps.add(it) }

        val textures = HashMap<String, Texture>()
        textureLumps.forEach { i ->
            val lumpInfo = wad.lumpInfos[i]
            io.seek(lumpInfo.filePos)
            val count = io.readUInt32()
            println("Loading $count textures")
            val offsets = Array(count.toInt()){ io.readInt32() }
            offsets.forEach { offset ->
                io.seek(lumpInfo.filePos + offset)
                val header = TextureHeader()
                header.read(io)
                println("Texture ${header.name} - Offset $offset")
                val patches = Array(header.numPatches.toInt()){ Patch().apply { this.read(io) } }
                val texture = Texture().apply {
                    this.textureHeader = header
                    this.patches = patches
                }
                textures[header.name] = texture
            }
        }
        return textures
    }

    private fun readFlatLumps(): HashMap<String, Flat> {
        val flats = HashMap<String, Flat>()
        val startLump = wad.lumps["F_START"] ?: throw Exception("F_START NOT FOUND!")
        val endLump = wad.lumps["F_END"] ?: throw Exception("F_END NOT FOUND!")
        (startLump until endLump).forEach { i ->
            val lumpInfo = wad.lumpInfos[i]
            io.seek(lumpInfo.filePos)

            val data = io.readBytes(4096)
            flats[lumpInfo.name] = Flat().apply {
                this.data = data
            }
        }

        return flats
    }

    fun loadTexture(name: String) : Texture? = wad.textures[name]

    fun loadImage(pName: Int) : Image? = wad.patches[wad.pNames[pName]]

    fun loadFlat(flatName: String) : Flat? = wad.flats[flatName]

    fun levelNames(): List<String> = wad.levels.map { it.key }

    fun readLevel(name: String): Level {
        val l = Level()
        val lIdx = wad.levels[name] ?: throw Exception("Level $name not found!")
        val s = lIdx+1
        val e = lIdx+11
        (s until e).forEach {  i ->
            val lInfo = wad.lumpInfos[i]
            io.seek(lInfo.filePos)

            val name = lInfo.name
            println("Loading lump $name")

            when(name) {
                "THINGS" -> l.things = readThings(lInfo)
                "SIDEDEFS" -> l.sideDefs = readSideDefs(lInfo)
                "LINEDEFS" -> l.lineDefs = readLineDefs(lInfo)
                "VERTEXES" -> l.vertexes = readVertexes(lInfo)
                "SEGS" -> l.segs = readSegs(lInfo)
                "SSECTORS" -> l.ssectors = readSSectors(lInfo)
                "NODES" -> l.nodes = readNodes(lInfo)
                "SECTORS" -> l.sectors = readSectors(lInfo)
                else -> println("Unhandled lump $name")
            }
        }
        return l
    }

    fun readThings(li: LumpInfo): Array<Thing> {
        val count = li.size / 10
        return Array<Thing>(count){ Thing().apply { this.read(io) } }
    }

    fun readLineDefs(li: LumpInfo): Array<LineDef> {
        val count = li.size / 14
        return Array(count){ LineDef().apply { this.read(io) } }
    }

    fun readSideDefs(li: LumpInfo): Array<SideDef> {
        val count = li.size / 30
        return Array(count){ SideDef().apply { this.read(io) } }
    }

    fun readVertexes(li: LumpInfo): Array<Vertex> {
        val count = li.size / 30
        return Array(count){ Vertex().apply { this.read(io) } }
    }

    fun readSSectors(li: LumpInfo): Array<SSector> {
        val count = li.size / 30
        return Array(count){ SSector().apply { this.read(io) } }
    }

    fun readNodes(li: LumpInfo): Array<Node> {
        val count = li.size / 30
        return Array(count){ Node().apply { this.read(io) } }
    }

    fun readSectors(li: LumpInfo): Array<Sector> {
        val count = li.size / 26
        return Array(count){ Sector().apply { this.read(io) } }
    }

    fun readSegs(li: LumpInfo): Array<Seg> {
        val count = li.size / 12
        return Array(count){ Seg().apply { this.read(io) } }
    }

}