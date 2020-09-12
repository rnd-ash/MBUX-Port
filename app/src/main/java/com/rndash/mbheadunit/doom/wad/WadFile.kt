package com.rndash.mbheadunit.doom.wad

import android.content.Context
import com.rndash.mbheadunit.doom.Logger.Companion.logDebug
import com.rndash.mbheadunit.doom.wad.structs.Header
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.rndash.mbheadunit.doom.wad.structs.Directory
import com.rndash.mbheadunit.doom.wad.structs.Image
import com.rndash.mbheadunit.doom.wad.structs.Palettes


@ExperimentalUnsignedTypes
class WadFile {
    private val data: ByteBuffer
    private lateinit var header: Header
    var pos = 0
    private lateinit var directories: Array<Directory>
    lateinit var colourPalettes: Palettes
    private var patches = HashMap<String, Image>()
    var sprites = HashMap<String, Image>()

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

    private fun extractPatchImage(dir: Directory) : Image {
        val ret = HashMap<String, Image>()
        seek(dir.filePos) // Seek to directories location in file
        val width = readUInt16().toInt()
        val height = readUInt16().toInt()
        val leftOffset = readUInt16()
        val topOffset = readUInt16()
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
        return Image(width, height, pixels)
    }

    private fun extractSprites(dirs: List<Directory>): HashMap<String, Image> {
        val ret = HashMap<String, Image>()
        dirs.forEach {
            ret[it.name] = extractPatchImage(it)
        }
        return ret
    }

    private fun extractThings(dir: Directory) {

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

    /**
     * Processes the WADs directories, to extract flats and sprites between
     * F_START - F_END and S_START - S_END
     */
    private fun processDirectories() {
        // Extract the 14 colour palettes from the WAD file
        val playpal = directories.first { it.name == "PLAYPAL" }
        colourPalettes = extractPlaypal(playpal)

        // TODO level extraction
        //val things: Directory = directories.first { it.name == "THINGS" }
        //extractThings(things) // Extract all things
        //logDebug("WAD_proc_dir", "Found things lump: Size: ${things.size} bytes")
        val patches: List<Directory> = extractVirtualFS("P_START", "P_END")
        logDebug("WAD_proc_dir", "Found ${patches.size} patches")

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