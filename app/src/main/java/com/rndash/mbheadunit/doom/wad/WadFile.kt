package com.rndash.mbheadunit.doom.wad

import android.content.Context
import android.util.Log
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.wad.Patch
import com.rndash.mbheadunit.doom.wad.mapData.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

@ExperimentalUnsignedTypes
class WadFile {

    val data: ByteBuffer
    lateinit var wadHeader: Info

    lateinit var files: ArrayList<FileLump>
    var levels = ArrayList<Level>()
    var lumps = ArrayList<LumpInfo>()

    constructor(f: File) {
        data = ByteBuffer.wrap(f.readBytes()).order(ByteOrder.LITTLE_ENDIAN)
        checkWad()
    }

    constructor(id: Int, ctx: Context) {
        data = ByteBuffer.wrap(ctx.resources.openRawResource(id).readBytes()).order(ByteOrder.LITTLE_ENDIAN)
        checkWad()
    }

    private fun checkWad() {
        data.rewind() // Start of wad file
        wadHeader = Info(
                readString(4),
                readInt(),
                readInt()
        )
        if (wadHeader.id != "IWAD" && wadHeader.id != "PWAD") {
            throw Exception("WAD Error. Header ${wadHeader.id} is not valid!")
        }
        println("Found valid WAD file. Header: ${wadHeader.id} and contains ${wadHeader.numLumps} lumps")
        seek(wadHeader.infoTableOffset)
        files = ArrayList(wadHeader.numLumps)
        (0 until wadHeader.numLumps).forEach {
            files.add(FileLump(readInt(), readInt(), readString8()))
            println("Adding file ${files[it].name}")
        }
        files.forEach {
            seek(it.filePos)
            lumps.add(LumpInfo(
                    it.name,
                    1,
                    it.filePos,
                    it.size
            ))
        }
    }

    private fun seek(offset: Int) { data.position(offset) }
    private fun readBytes(num: Int): ByteArray = ByteArray(num).apply { data.get(this) }
    private fun readInt() : Int = data.int
    private fun readUInt() : UInt = data.int.toUInt()
    private fun readShort() : Short = data.short
    private fun readUShort(): UShort = data.short.toUShort()
    private fun readByte() : Byte = data.get()
    private fun readUByte() : UByte = data.get().toUByte()

    private fun readString(charCount: Int): String = String(readBytes(charCount))
    private fun readString8(): String = readString(8).let { s ->
        return String(s.toMutableList()
                .map { it.toByte() }
                .dropLastWhile { it > 127 || it < 32 }
                .toByteArray()
        )
    }

    /**
     * Temp class for readTextures function
     */
    inner class PatchTemp(
        val XOffset: Short,
        val YOffset: Short,
        val pNameNumber: Short,
        val stepDir: Short,
        val cMap: Short
    )

    private var textures = HashMap<String, Texture>()
    fun readTextures() {
        // First of all work out all the patch names
        val pNames = files.dropWhile { it.name != "P_START" }.dropLastWhile { it.name != "P_END" }
                .toMutableList()
                .drop(2)
                .dropLast(2)
                .map { it.name }

        println(pNames.joinToString(", "))


        val l1 = lumps.firstOrNull { it.name == "TEXTURE1" } ?: throw Exception("No texture lump found!?")
        val lumps = lumps.firstOrNull { it.name == "TEXTURE2" }.let {
            if (it == null) {
                arrayOf(l1)
            } else {
                arrayOf(l1, it)
            }
        }
        lumps.forEach { l ->
            seek(l.position)
            val count = readUInt().toInt()
            val offsets = Array(count) { readUInt().toInt() }
            offsets.forEach{ offset ->
                seek(l.position + offset)
                val header = TextureHeader(readString8(), readInt(), readShort(), readShort(), readInt(), readShort())
                val patches = Array(header.numPatches.toInt()) {
                    PatchTemp(readShort(), readShort(), readShort(), readShort(), readShort())
                }
                textures[header.name] = Texture(header, patches.map { readPatch(pNames[it.pNameNumber.toInt()]) }.toTypedArray())
            }
        }
    }

    fun getTexture(name: String) = textures[name] ?: throw Exception("Texture $name not found!")


    fun getNumLumps() : Int = lumps.size

    fun loadLevels() {
        val names = ArrayList<String>()
        (0 until files.size).forEach {
            if (files[it].name == "THINGS") {
                names.add(files[it-1].name)
            }
        }
        println("Found levels: $names")
        names.forEach { levelName ->
            val things = extractThings(getLumpFromLevel("THINGS", levelName) ?: return@forEach)
            things.apply {
                println("")
            }
            val sdefs = extractSideDefs(getLumpFromLevel("SIDEDEFS", levelName) ?: return@forEach)
            val ldefs = extractLineDefs(getLumpFromLevel("LINEDEFS", levelName) ?: return@forEach)
            val vertexes = extractVertexes(getLumpFromLevel("VERTEXES", levelName) ?: return@forEach)
            val segs = extractSegs(getLumpFromLevel("SEGS", levelName) ?: return@forEach)
            val subsectors = extractSubSectors(getLumpFromLevel("SSECTORS", levelName) ?: return@forEach)
            val nodes = extractNodes(getLumpFromLevel("NODES", levelName) ?: return@forEach)
            val sectors = extractSectors(getLumpFromLevel("SECTORS", levelName) ?: return@forEach)
            levels.add(Level(levelName, things, ldefs, sdefs, vertexes, segs, subsectors, nodes, sectors))
        }
    }

    private fun getLumpFromLevel(name: String, level: String): FileLump? {
        files.indexOfFirst { it.name == level }.let { index ->
            (index until index + 11).forEach {
                if (files[it].name == name) {
                    return files[it]
                }
            }
        }
        Log.e("Wad-LevelLoader", "Lump $name not found in level $level")
        return null
    }

    private fun extractThings(l: FileLump): Array<Thing> {
        seek(l.filePos)
        return Array(l.size / THING_SIZE_BYTES) {
            Thing(readShort(), readShort(), readShort(), readShort(), readShort())
        }
    }

    private fun extractSideDefs(l: FileLump): Array<SideDef> {
        seek(l.filePos)
        return Array(l.size / SIDEDEF_SIZE_BYTES) {
            SideDef(readShort(), readShort(), readString8(), readString8(), readString8(), readShort())
        }
    }

    private fun extractLineDefs(l: FileLump): Array<LineDef> {
        seek(l.filePos)
        return Array(l.size / LINDEF_SIZE_BYTES) {
            LineDef(readShort(), readShort(), readShort(), readShort(), readShort(), readShort(), readShort())
        }
    }

    private fun extractVertexes(l: FileLump): Array<Vertex> {
        seek(l.filePos)
        return Array(l.size / VERTEX_SIZE_BYTES) {
            Vertex(readShort(), readShort())
        }
    }

    private fun extractSegs(l: FileLump): Array<Seg> {
        seek(l.filePos)
        return Array(l.size / SEG_SIZE_BYTES) {
            Seg(readShort(), readShort(), readShort(), readShort(), readShort(), readShort())
        }
    }

    private fun extractSubSectors(l: FileLump): Array<SubSector> {
        seek(l.filePos)
        return Array(l.size / SUBSECTOR_SIZE_BYTES) {
            SubSector(readShort(), readShort())
        }
    }

    private fun extractNodes(l: FileLump): Array<Node> {
        seek(l.filePos)
        return Array(l.size / NODE_SIZE_BYTES) {
            Node(
                readShort(), readShort(), readShort(), readShort(),
                Array(2) { Bbox(readShort(), readShort(), readShort(), readShort()) },
                ShortArray(2) { readShort() }
            )
        }
    }

    private fun extractSectors(l: FileLump): Array<Sector> {
        seek(l.filePos)
        return Array(l.size / SECTOR_SIZE_BYTES) {
            Sector(readShort(), readShort(), readString8(), readString8(), readShort(), readShort(), readShort())
        }
    }


    fun getLevelNames() : List<String> = this.levels.map { it.name }

    fun getLevel(name: String): Level {
        return this.levels.firstOrNull { it.name == name } ?: throw Exception("Level $name not found!?")
    }



    /**
     * Returns the lump ID by a given name
     * Returns -1 if lump name is not found
     */
    fun checkLumpIDByName(name: String): Int {
        lumps.indices.reversed().forEach {
            if (lumps[it].name.startsWith(name)) {
                return it
            }
        }
        return -1
    }

    fun readPalette() : Array<ColourMap> {
        checkLumpIDByName("PLAYPAL").let {
            if (it == -1) {
                throw Exception("Colour palette PLAYPAL not found!")
            } else {
                val f = lumps[it]
                seek(f.position)
                return Array(14){ColourMap(readBytes(256*3))}
            }
        }
    }

    fun getLumpIDByName(name: String): Int {
        checkLumpIDByName(name).let {
            if (it == -1) {
                throw Exception("Lump name $name not found")
            }
            return it
        }
    }

    fun getLumpLength(lumpID: Int): Int {
        if (lumpID >= lumps.size) {
            throw Exception("$lumpID >= lump count (${lumps.size})")
        }
        return lumps[lumpID].size
    }

    fun readPatches(match: String): Array<Patch> {
        return files
                .filter { it.name.startsWith(match) } // Filter what matches our query string
                .sortedBy { it.name } // Sort alpha-numerically
                .map { readPatch(it.name) } // Map each one to a patch
                .toTypedArray() // Turn to array
    }

    fun readPatch(name: String): Patch {
        println("Reading patch $name")
        checkLumpIDByName(name).let {
            if (it == -1) {
                throw Exception("Patch $name not found!")
            } else {
                val p = files[it]
                seek(p.filePos)
                val ph = PicHeader(
                        readShort(), // Width
                        readShort(), // Height
                        readShort(), // Left offset
                        readShort(), // Right offset
                )
                val offsets = IntArray(ph.width.toInt()) { readUInt().toInt() }
                val pixels = ByteArray(ph.width*ph.height)

                offsets.forEachIndexed { colIndex, offset ->
                    seek(p.filePos + offset) // Seek to offset column
                    var rowStart = 0
                    while(rowStart != 0xFF) {
                        rowStart = readUByte().toInt()
                        if (rowStart == 0xFF) { break }
                        val pixelCount = readUByte().toInt()
                        readByte() // Dummy
                        (0 until pixelCount).forEach { p ->
                            // Read a pixel pointer, and store it in the images array
                            pixels[(p + rowStart) * ph.width+colIndex] = readByte()
                        }
                        readByte() // Dummy
                    }
                }
                return Patch(ph.width, ph.height, ph.leftOffset, ph.topOffset, pixels)
            }
        }
    }
}