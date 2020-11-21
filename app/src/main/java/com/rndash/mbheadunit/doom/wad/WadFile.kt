package com.rndash.mbheadunit.doom.wad

import android.util.Log
import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.wad.mapData.*
import org.w3c.dom.Text
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

@ExperimentalUnsignedTypes
class WadFile {

    val data: ByteBuffer
    lateinit var wadHeader: Info

    lateinit var lumps: ArrayList<FileLump>
    var levels = ArrayList<Level>()
    lateinit var patchNames: Array<String>

    constructor(f: File) {
        data = ByteBuffer.allocateDirect(f.length().toInt()).run {
            put(f.readBytes())
            order(ByteOrder.LITTLE_ENDIAN)
        }
        checkWad()
    }

    private fun checkWad() {
        seek(0)
        wadHeader = Info(
                readString(4),
                readInt(),
                readInt()
        )
        if (wadHeader.id != "IWAD" && wadHeader.id != "PWAD") {
            throw Exception("WAD Error. Header ${wadHeader.id} is not valid!")
        }
        seek(wadHeader.infoTableOffset)
        lumps = ArrayList(wadHeader.numLumps)
        (0 until wadHeader.numLumps).forEach { _ ->
            lumps.add(FileLump(readInt(), readInt(), readString8()))
        }

        // Now cache patch names
        val pNames = lumps.firstOrNull { it.name == "PNAMES" }
        if (pNames == null) {
            throw Exception("No PNAMES lump found in WAD!")
        } else {
            seek(pNames.filePos)
            val count = readUInt()
            patchNames = Array(count.toInt()) { readString8() }
            readTextures()
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

    private val textures = HashMap<String, Texture>()
    fun readTextures() {
        // First of all work out all the patch names
        val l1 = lumps.firstOrNull { it.name == "TEXTURE1" } ?: throw Exception("No texture lump found!?")
        val lumps = lumps.firstOrNull { it.name == "TEXTURE2" }.let {
            if (it == null) {
                arrayOf(l1)
            } else {
                arrayOf(l1, it)
            }
        }
        lumps.forEach { l ->
            seek(l.filePos)
            val count = readUInt().toInt()
            val offsets = Array(count) { readInt() }
            offsets.forEach{ offset ->
                seek(l.filePos + offset)
                val header = TextureHeader(readString8().toUpperCase(), readInt(), readShort(), readShort(), readInt(), readShort())
                val patches = Array(header.numPatches.toInt()) {
                    TexturePatch(readShort().toInt(), readShort().toInt(), readShort().toInt(), readShort().toInt(), readShort().toInt())
                }
                textures[header.name] = Texture(header, patches)
            }
        }
        println("Loaded ${textures.size} textures")
    }

    fun readFlat(name: String):  ByteBuffer {
        getLumpIDByName(name).let {
            if (it == -1) {
                throw Exception("Cannot find flat $name")
            } else {
                val lump = lumps[it]
                seek(lump.filePos)
                return ByteBuffer.allocateDirect(4096).apply {
                    put(readBytes(4096))
                    position(0)
                }
            }
        }
    }

    fun cacheTexture(name: String) : Texture? {
        return textures[name]
    }


    fun getNumLumps() : Int = lumps.size

    val levelNames = ArrayList<String>()
    fun loadLevels() {
        (0 until lumps.size).forEach {
            if (lumps[it].name == "THINGS") {
                levelNames.add(lumps[it-1].name)
            }
        }
    }

    private fun getLumpFromLevel(name: String, level: String): FileLump {
        lumps.indexOfFirst { it.name == level }.let { index ->
            (index until index + 11).forEach {
                if (lumps[it].name == name) {
                    return lumps[it]
                }
            }
        }
        throw Exception("Lump $name not found in level $level")
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

    fun getLump(name: String): ByteArray {
        return lumps.first { it.name == name }.let {
            seek(it.filePos)
            readBytes(it.size)
        }
    }

    /**
     * Access MIDI Created with File(Environment.getExternalStorageDirectory(), "tmp.mid")
     */
    fun getMidi(name: String): Boolean {
        return lumps.first { it.name == name }.let {
            seek(it.filePos)
            readBytes(it.size)
        }.let { Mus(it).toMidi() }
    }


    fun getLevelNames() : List<String> = this.levelNames

    fun getLevel(name: String): Level {
        val levelName = this.levelNames.firstOrNull { it == name } ?: throw Exception("Level $name not found!?")
        val things = extractThings(getLumpFromLevel("THINGS", levelName))
        val sdefs = extractSideDefs(getLumpFromLevel("SIDEDEFS", levelName))
        val ldefs = extractLineDefs(getLumpFromLevel("LINEDEFS", levelName))
        val vertexes = extractVertexes(getLumpFromLevel("VERTEXES", levelName))
        val segs = extractSegs(getLumpFromLevel("SEGS", levelName))
        val subsectors = extractSubSectors(getLumpFromLevel("SSECTORS", levelName))
        val nodes = extractNodes(getLumpFromLevel("NODES", levelName))
        val sectors = extractSectors(getLumpFromLevel("SECTORS", levelName))
        return Level(levelName, things, ldefs, sdefs, vertexes, segs, subsectors, nodes, sectors)
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
                seek(f.filePos)
                return Array(14){
                    ColourMap(ByteBuffer.allocateDirect(256*3).apply {
                        this.put(readBytes(256*3), 0, 256*3)
                    })
                }
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
        return lumps
                .filter { it.name.startsWith(match) } // Filter what matches our query string
                .sortedBy { it.name } // Sort alpha-numerically
                .map { readPatch(it.name) } // Map each one to a patch
                .toTypedArray() // Turn to array
    }

    fun readPatch(name: String): Patch {
        checkLumpIDByName(name).let {
            if (it == -1) {
                throw Exception("Patch $name not found")
            } else {
                val p = lumps[it]
                seek(p.filePos)
                val ph = PicHeader(
                    readShort().toInt(), // Width
                    readShort().toInt(), // Height
                        readShort().toInt(), // Left offset
                        readShort().toInt(), // Right offset
                )
                val offsets = IntArray(ph.width) { readUInt().toInt() }
                val pixels = ByteBuffer.allocateDirect(ph.width*ph.height)
                for (px in 0 until ph.width*ph.height) {
                    pixels.put(px, 0xFF.toByte())
                }
                offsets.forEachIndexed { colIndex, offset ->
                    seek(p.filePos + offset) // Seek to offset column
                    var rowStart = 0
                    while(rowStart != 0xFF) {
                        rowStart = readUByte().toInt()
                        if (rowStart == 0xFF) { break }
                        val pixelCount = readUByte().toInt()
                        readByte() // Dummy
                        for(pix in 0 until pixelCount) {
                            // Read a pixel pointer, and store it in the images array
                            pixels.put(((pix + rowStart)*ph.width)+colIndex, data.get())
                        }
                        readByte() // Dummy
                    }
                }
                return Patch(name, ph.width, ph.height, ph.leftOffset, ph.topOffset, pixels)
            }
        }
    }
}