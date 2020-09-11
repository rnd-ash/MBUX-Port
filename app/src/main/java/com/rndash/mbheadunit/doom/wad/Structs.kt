package com.rndash.mbheadunit.doom.wad


// Custom data types
typealias Int16 = Short
typealias Int32 = Int
@ExperimentalUnsignedTypes
typealias UInt32 = UInt
@ExperimentalUnsignedTypes
typealias UInt16 = UShort


/**
 * Translated to Kotlin from https://github.com/penberg/godoom/blob/master/wad.go
 */


enum class StructSizeBytes(val size: Int) {
    WAD(0), // Virtual so no read
    HEADER(12)
}

@ExperimentalStdlibApi
class WAD {
    lateinit var header: Header
    lateinit var pNames: Array<String>
    lateinit var patches: Map<String, Image>
    var transparentPalleteIndex: Byte = 0
    lateinit var playPal: PlayPal
    lateinit var textures: Map<String, Texture>
    lateinit var flats: Map<String, Flat>
    lateinit var levels: Map<String, Int>
    lateinit var lumps: Map<String, Int>
    lateinit var lumpInfos: Array<LumpInfo>
}


@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Header : Struct {
    var magic = ByteArray(4)
    var numLumps: Int32 = 0
    var infoTableOffset: Int32 = 0

    override fun read(w: DoomFileIO) {
        magic = w.readBytes(4)
        numLumps = w.readInt32()
        infoTableOffset = w.readInt32()
        println("HEADER")
        println("${numLumps} ${infoTableOffset}")
    }
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class LumpInfo : Struct {
    var filePos: Int32 = 0
    var size: Int32 = 0
    lateinit var name: String

    override fun read(w: DoomFileIO) {
        filePos = w.readInt32()
        size = w.readInt32()
        name = w.readString(8)
    }
}

@ExperimentalStdlibApi
class Image {
    var width: Int = 0
    var height: Int = 0
    lateinit var pixels: ByteArray
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Patch: Struct {
    var xOffset: Int16 = 0
    var yOffset: Int16 = 0
    var pNameNumber: Int16 = 0
    var stepDir: Int16 = 0
    var colourMap: Int16 = 0
    override fun read(w: DoomFileIO) {
        xOffset = w.readInt16()
        yOffset = w.readInt16()
        pNameNumber = w.readInt16()
        stepDir = w.readInt16()
        colourMap = w.readInt16()
    }
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Texture {
    lateinit var textureHeader: TextureHeader
    lateinit var patches: Array<Patch>
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class PictureHeader: Struct {
    var width: Int16 = 0
    var height: Int16 = 0
    var leftOffset: Int16 = 0
    var topOffset: Int16 = 0
    lateinit var columnOffsets: Array<Int32>
    override fun read(w: DoomFileIO) {
        width = w.readInt16()
        height = w.readInt16()
        leftOffset = w.readInt16()
        topOffset = w.readInt16()
        columnOffsets = Array(width.toInt()){w.readInt32()}
    }
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class TextureHeader(): Struct {
    lateinit var name: String
    var mask: Int32 = 0
    var width: Int16 = 0
    var height: Int16 = 0
    var columnDirectory: Int32 = 0
    var numPatches: Int16 = 0


    override fun read(w: DoomFileIO) {
        name = w.readString(8)
        mask = w.readInt32()
        width = w.readInt16()
        height = w.readInt16()
        columnDirectory = w.readInt32()
        numPatches = w.readInt16()
    }
}

class Flat {
    lateinit var data: ByteArray
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Level {
    lateinit var things: Array<Thing>
    lateinit var lineDefs: Array<LineDef>
    lateinit var sideDefs: Array<SideDef>
    lateinit var vertexes: Array<Vertex>
    lateinit var segs: Array<Seg>
    lateinit var ssectors: Array<SSector>
    lateinit var nodes: Array<Node>
    lateinit var sectors: Array<Sector>
}

/**
 * Size bytes: 10
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Thing : Struct {
    var xPosition: Int16 = 0
    var yPosition: Int16 = 0
    var angle: Int16 = 0
    var type: Int16 = 0
    var options: Int16 = 0
    override fun read(w: DoomFileIO) {
        xPosition = w.readInt16()
        yPosition = w.readInt16()
        angle = w.readInt16()
        type = w.readInt16()
        options = w.readInt16()
    }
}

/**
 * Size bytes: 14
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class LineDef : Struct {
    var vertexStart: Int16 = 0
    var vertexEnd: Int16 = 0
    var flags: Int16 = 0
    var function: Int16 = 0
    var tag: Int16 = 0
    var sideDefRight: Int16 = 0
    var sideDefLeft: Int16 = 0
    override fun read(w: DoomFileIO) {
        vertexStart = w.readInt16()
        vertexEnd = w.readInt16()
        flags = w.readInt16()
        function = w.readInt16()
        tag = w.readInt16()
        sideDefRight = w.readInt16()
        sideDefLeft = w.readInt16()
    }
}

/**
 * Size 30 bytes
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class SideDef : Struct {
    var xOffset: Int16 = 0
    var yOffset: Int16 = 0
    lateinit var upperTexture: String
    lateinit var lowerTexture: String
    lateinit var middleTexture: String
    var sectorRef: Int16 = 0
    override fun read(w: DoomFileIO) {
        xOffset = w.readInt16()
        yOffset = w.readInt16()
        upperTexture = w.readString(8)
        lowerTexture = w.readString(8)
        middleTexture = w.readString(8)
        sectorRef = w.readInt16()
    }
}

/**
 * Size bytes 4
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Vertex(): Struct {
    var x: Int16 = 0
    var y: Int16 = 0
    override fun read(w: DoomFileIO) {
        x = w.readInt16()
        y = w.readInt16()
    }
}

/**
 * Size bytes 12
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Seg : Struct {
    var vertexStart: Int16 = 0
    var vertexEnd: Int16 = 0
    var bams: Int16 = 0
    var lineNum: Int16 = 0
    var segSide: Int16 = 0
    var segOffset: Int16 = 0
    override fun read(w: DoomFileIO) {
        vertexStart = w.readInt16()
        vertexEnd = w.readInt16()
        bams = w.readInt16()
        lineNum = w.readInt16()
        segSide = w.readInt16()
        segOffset = w.readInt16()
    }
}

/**
 * Size bytes 4
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class SSector() : Struct {
    var numSegs: Int16 = 0
    var startSeg: Int16 = 0
    override fun read(w: DoomFileIO) {
        numSegs = w.readInt16()
        startSeg = w.readInt16()
    }
}

/**
 * Size bytes 8
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class BBox : Struct {
    var top: Int16 = 0
    var bottom: Int16 = 0
    var left: Int16 = 0
    var right: Int16 = 0
    override fun read(w: DoomFileIO) {
        top = w.readInt16()
        bottom = w.readInt16()
        left = w.readInt16()
        right = w.readInt16()
    }
}

/**
 * Size bytes 8 + (Arrays)
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Node: Struct {
    var x: Int16 = 0
    var y: Int16 = 0
    var dx: Int16 = 0
    var dy: Int16 = 0
    lateinit var bbox: Array<BBox>
    lateinit var child: Array<Int16>
    override fun read(w: DoomFileIO) {
        x = w.readInt16()
        y = w.readInt16()
        dx = w.readInt16()
        dy = w.readInt16()
        bbox = Array(2){BBox().apply  { this.read(w) }}
        child = Array(2){ w.readInt16() }
    }
}

/**
 * Size bytes 26
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Sector : Struct {
    var floorHeight: Int16 = 0
    var ceilingHeight: Int16 = 0
    lateinit var floorPic: String
    lateinit var ceilingPic: String
    var lightLevel: Int16 = 0
    var specialSector: Int16 = 0
    var tag: Int16 = 0
    override fun read(w: DoomFileIO) {
        floorHeight = w.readInt16()
        ceilingHeight = w.readInt16()
        floorPic = w.readString(8)
        ceilingPic = w.readString(8)
        lightLevel = w.readInt16()
        specialSector = w.readInt16()
        tag = w.readInt16()
    }
}

class Reject
class BlockMap
data class RGB(var r: Byte, var g: Byte, var b: Byte){}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Palette: Struct {
    val table = Array<RGB>(255){RGB(0,0,0)}
    override fun read(w: DoomFileIO) {
        val bytes = w.readBytes(255*3)
        (0 until 255).forEach {
            table[it].r = bytes[it*3 + 0]
            table[it].g = bytes[it*3 + 1]
            table[it].b = bytes[it*3 + 2]
        }
    }
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class PlayPal: Struct {
    var palette = Array<Palette>(14){ Palette() }
    override fun read(w: DoomFileIO) {
        (0 until 14).forEach {
            palette[it].read(w)
        }
    }
}