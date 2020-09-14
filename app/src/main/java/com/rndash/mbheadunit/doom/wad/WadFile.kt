package com.rndash.mbheadunit.doom.wad

import android.content.Context
import com.rndash.mbheadunit.doom.Logger.Companion.logDebug
import com.rndash.mbheadunit.doom.Logger.Companion.logInfo
import com.rndash.mbheadunit.doom.wad.structs.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder


@ExperimentalUnsignedTypes
class WadFile {
    private val data: ByteBuffer
    private lateinit var header: Header
    var pos = 0
    lateinit var directories: Array<Directory>
    lateinit var colourPalettes: Palettes
    var patches = HashMap<String, PatchImage>()
    var textures = HashMap<String, Texture>()
    lateinit var levels: Array<LevelData>
    var sprites = HashMap<String, PatchImage>()
    lateinit var patchNames: Array<String>

    constructor(file: File) {
        data = ByteBuffer.wrap(file.readBytes()).order(ByteOrder.LITTLE_ENDIAN)
        checkHeader()
    }

    constructor(resID: Int, ctx: Context) {
        data = ByteBuffer.wrap(ctx.resources.openRawResource(resID).readBytes()).order(ByteOrder.LITTLE_ENDIAN)
        checkHeader()
    }

    private fun seek(p: Int) {
        pos = p
        data.position(p)
    }

    private fun checkHeader() {
        val h = readHeader()
        logDebug("WAD", "WAD Header: $h")
        if (h.identification != "IWAD") {
            throw Exception("Unknown WAD type '${h.identification}', only IWAD is supported")
        }
        this.header = h
    }

    // Read a byte segment from WAD file
    private fun readBytes(size: Int): ByteArray {
        val b = ByteArray(size)
        data.get(b)
        return b
    }

    // Read 32bit signed int from WAD file
    private fun readInt32() = data.int
    // Read 32bit unsigned int from WAD file
    private fun readUInt32() = readInt32().toUInt()

    // Read 16bit signed int from WAD file
    private fun readInt16() = data.short
    // Read 16bit unsigned int from WAD file
    private fun readUInt16() = readInt16().toUShort()

    // Read signed byte from WAD file
    private fun readByte() = data.get()
    // Read unsigned byte from WAD file
    private fun readUInt8() = readByte().toUByte()

    // Read 8bit string from WAD file, and remove padding bytes if present
    private fun readString8() : String {
        val bytes = readBytes(8).toMutableList()
        bytes.removeIf { it < 32 || it > 127 }
        return String(bytes.toByteArray(), Charsets.US_ASCII)
    }

    /**
     * Returns the WAD file header
     */
    private fun readHeader() : Header {
        seek(0) // Start of file
        return Header(readBytes(4), readInt32(), readInt32())
    }

    /**
     * Reads the directory data from the WAD file - Returns a list of Directories from the WAD
     */
    private fun readDirectories(): Array<Directory> {
        seek(header.infoTableOffset)
        return (0 until header.numLumps).map {
            Directory(readInt32(), readInt32(), readString8())
        }.toTypedArray()
    }

    /**
     * Extracts lumps between virtual directory's start and end flags
     * For Flats, its F_START and F_END
     * For Sprites, its S_START and S_END
     */
    private fun extractVirtualFS(start: String, end: String): List<Directory> {
        return directories
            .dropWhile { it.name != start } // Drop all dirs that are before the start
            .dropLastWhile { it.name != end } // Drop all dirs after the end
            .drop(1) // Remove start
            .dropLast(1) // Remove end
    }

    private fun extractFirstAfter(after: String, targName: String) : Directory? {
        return directories.dropWhile { it.name != after }.firstOrNull { it.name == targName }
    }

    private fun extractPatchImage(dir: Directory) : PatchImage {
        seek(dir.filePos) // Seek to directories location in file
        val width = readInt16().toInt()
        val height = readInt16().toInt()
        val leftOffset = readInt16()
        val topOffset = readInt16()
        val columnsOffsets = Array(width) { readUInt32() }
        //logDebug("WAD-patch-extract", "Extracting patch ${dir.name}. Size $width x $height")
        val pixels = UByteArray(width * height){0xFF.toUByte()} // Not RGB Pixels, but pointers to the colour map
        columnsOffsets.forEachIndexed { colIndex, offset ->
            seek(dir.filePos + offset.toInt()) // Seek to offset column
            var rowStart = 0
            while(rowStart != 0xFF) {
                rowStart = readUInt8().toInt()
                if (rowStart == 0xFF) { break }
                val pixelCount = readUInt8().toInt()
                readUInt8() // Dummy
                (0 until pixelCount).forEach {
                    // Read a pixel pointer, and store it in the images array
                    pixels[(it + rowStart) * width+colIndex] = readUInt8()
                }
                readByte() // Dummy
            }
        }
        return PatchImage(width, height, pixels.toByteArray(), leftOffset.toUShort(), topOffset.toUShort(), dir.name)
    }

    fun extractPatchByName(name: String) : PatchImage? {
        return try {
            extractPatchImage(directories.first { it.name == name })
        } catch (e: Exception) {
            System.err.println("Cannot extract $name due to $e")
            null
        }
    }

    fun extractPatchesByName(matchString: String) : Array<PatchImage> {
        return try {
            directories.filter { it.name.startsWith(matchString) }.map {
                extractPatchImage(it)
            }.toTypedArray()
        } catch (e: Exception) {
            arrayOf()
        }
    }

    private fun extractSprites(dirs: List<Directory>): HashMap<String, PatchImage> {
        val ret = HashMap<String, PatchImage>()
        dirs.forEach {
            ret[it.name] = extractPatchImage(it)
        }
        return ret
    }

    private inline fun <reified T: Struct> extractType(dir: Directory, sizeStruct: Int, map: () -> T) : Array<T> {
        seek(dir.filePos)
        val extractCount = dir.size / sizeStruct
        val list = ArrayList<T>()
        (0 until extractCount).forEach { _ ->
            list.add(map())
        }
        return list.toTypedArray()
    }

    private fun extractLevel(levelName: String) : LevelData {
        val l = LevelData(levelName)
        val things = extractFirstAfter(levelName, "THINGS") ?: throw Exception("$levelName THINGS not found")
        l.things = extractType(things, 10) {
            Thing(readInt16(), readInt16(), readUInt16(), readUInt16(), readUInt16())
        }

        val linedefs = extractFirstAfter(levelName, "LINEDEFS") ?: throw Exception("$levelName LINEDEFS not found")
        l.lineDefs = extractType(linedefs, 14) {
            LineDef(readInt16(), readInt16(), readInt16(), readInt16(), readInt16(), readInt16(), readInt16())
        }

        val sidedefs = extractFirstAfter(levelName, "SIDEDEFS") ?: throw Exception("$levelName SIDEDEFS not found")
        l.sideDefs = extractType(sidedefs, 30) {
            SideDef(readInt16(), readInt16(), readString8(), readString8(), readString8(), readUInt16())
        }

        val vertexes = extractFirstAfter(levelName, "VERTEXES") ?: throw Exception("$levelName VERTEXES not found")
        l.vertexes = extractType(vertexes, 4) {
            Vertex(readInt16(), readInt16())
        }

        val segs = extractFirstAfter(levelName, "SEGS") ?: throw Exception("$levelName SEGS not found")
        l.segs = extractType(segs, 12) {
            Seg(readInt16(), readInt16(), readInt16(), readUInt16(), readInt16(), readInt16())
        }

        val ssectors = extractFirstAfter(levelName, "SSECTORS") ?: throw Exception("$levelName SSECTORS not found")
        l.ssectors = extractType(ssectors, 4) {
            SSector(readInt16(), readInt16())
        }

        val nodes = extractFirstAfter(levelName, "NODES") ?: throw Exception("$levelName NDOES not found")
        l.nodes = extractType(nodes, 28) {
            Node(readInt16(), readInt16(), readInt16(), readInt16()).apply {
                this.bbox = Array(2) { BBox(readInt16(), readInt16(), readInt16(), readInt16()) }
                this.child = Array(2) { readInt16() }
            }
        }

        val sectors = extractFirstAfter(levelName, "SECTORS") ?: throw Exception("$levelName SECTORS not found")
        l.sectors = extractType(sectors, 26) {
            Sector(readInt16(), readInt16(), readString8(), readString8(), readInt16(), readUInt16(), readUInt16())
        }

        // Ignore REJECT block - Not needed as AI is fast on modern HW

        // Ignore BLOCKMAP block - little documentation on how this works TODO blockmap collision detection
        //val blockmap = extractFirstAfter(levelName, "BLOCKMAP") ?: throw Exception("$levelName BLOCKMAP not found")


        // Ignore BEHAVIOUR block - Not used in Vanilla DOOM
        return l
    }

    private fun extractPlaypal(dir: Directory) : Palettes {
        seek(dir.filePos)
        val p = Palettes()
        // 14 palettes within game (ALWAYS)
        (0 until 14).forEach {
            // 256 palettes
            (0 until 256).forEach { c ->
                p.data[it].red[c] = readUInt8()
                p.data[it].green[c] = readUInt8()
                p.data[it].blue[c] = readUInt8()
            }
        }
        return p
    }

    private fun extractTextures(dirs: List<Directory>): HashMap<String, Texture> {
        val ret = HashMap<String, Texture>()
        dirs.forEach { lump ->
            seek(lump.filePos)
            val count = readUInt32()
            // Offset for each texture
            val offsets = Array(count.toInt()){ readInt32() }
            offsets.forEach { offset ->
                seek(lump.filePos + offset)
                val header = TextureHeader(readString8(), readInt32(), readInt16(), readInt16(), readInt32(), readUInt16())
                val patches = Array(header.numPatches.toInt()) {
                    Patch(readInt16(), readInt16(), readInt16(), readInt16(), readInt16())
                }
                val tex = Texture(header, patches)
                ret[header.name] = tex
            }
        }
        return ret
    }

    private fun extractPatches() {
        patchNames.forEach { pName ->
            val lump = directories.firstOrNull { it.name == pName } ?: return@forEach // Ignore missing
            patches[pName] = extractPatchImage(lump)
        }
    }

    /**
     * Processes the WADs directories, to extract flats and sprites between
     * F_START - F_END and S_START - S_END
     */
    private fun processDirectories() {
        // Extract the 14 colour palettes from the WAD file
        val playpal = directories.first { it.name == "PLAYPAL" }
        colourPalettes = extractPlaypal(playpal)

        val levels = ArrayList<LevelData>()
        directories.forEachIndexed { index, directory ->
            if (directory.name == "THINGS") {
                levels.add(extractLevel(directories[index-1].name))
            }
        }
        this.levels = levels.toTypedArray()
        logInfo("WAD_READ", "Read ${this.levels.size} levels OK!")

        this.patchNames = directories.first { it.name == "PNAMES" }.let {
            seek(it.filePos)
            val count = readUInt32()
            logInfo("WAD_READ", "Read $count patch names")
            Array(count.toInt()) { readString8() }
        }
        extractPatches()

        val patches: List<Directory> = extractVirtualFS("P_START", "P_END")
        logInfo("WAD_READ", "Read ${patches.size} patches OK!")

        // Extract all textures from WAD
        this.textures = extractTextures(directories.filter { it.name.startsWith("TEXTURE") })
        logInfo("WAD_READ", "Read ${this.textures.size} textures OK!")


        val flats: List<Directory> = extractVirtualFS("F_START", "F_END")
        logDebug("WAD_proc_dir", "Found ${flats.size} flats")

        val sprites: List<Directory> = extractVirtualFS("S_START", "S_END")
        logDebug("WAD_proc_dir", "Found ${sprites.size} sprites")
        this.sprites = extractSprites(sprites)
    }

    fun readWad() {
        this.directories = readDirectories() // Read WAD's directories
        processDirectories()
    }
}