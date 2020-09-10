package com.rndash.mbheadunit.doom.wad
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes


// Custom data types
typealias Int16 = UShort
typealias Int32 = UInt


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
}


@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Header : Struct {
    var magic = ByteArray(4)
    var numLumps: Int32 = 0U
    var infoTableOffset: Int32 = 0U

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
    var filePos: Int32 = 0U
    var size: Int32 = 0U
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

class Patch {
    var xOffset: Int = 0
    var yOffset: Int = 0
    var pNameNumber: Int = 0
    var stepDir: Int = 0
    var colourMap: Int = 0
}

@ExperimentalStdlibApi
class Texture {
    lateinit var textureHeader: Header
    lateinit var patches: Array<Patch>
}

class PictureHeader {
    var width: Int = 0
    var height: Int = 0
    var leftOffset: Int = 0
    var topOffset: Int = 0
}

class Flat {
    lateinit var data: ByteArray
}

class Level {

}

class Thing {
    var xPosition: Int = 0
    var yPosition: Int = 0
    var angle: Int = 0
    var type: Int = 0
    var options: Int = 0
}

class LineDef {
    var vertexStart: Int = 0
    var vertexEnd: Int = 0
    var flags: Int = 0
    var function: Int = 0
    var tag: Int = 0
    var sideDefRight: Int = 0
    var sideDefLeft: Int = 0
}

class SideDef {
    var xOffset: Int = 0
    var yOffset: Int = 0
    lateinit var upperTexture: String
    lateinit var lowerTexture: String
    lateinit var middleTexture: String
    var sectorRef: Int = 0
}

class Vertex(var x: Int, var y: Int)

class Seg {
    var vertexStart: Int = 0
    var vertexEnd: Int = 0
    var bams: Int = 0
    var lineNum: Int = 0
    var segSide: Int = 0
    var segOffset: Int = 0
}

class SSector(var numSegs: Int, var startSeg: Int)

class BBox {
    var top: Int = 0
    var bottom: Int = 0
    var left: Int = 0
    var right: Int = 0
}

class Node {
    var x: Int = 0
    var y: Int = 0
    var dx: Int = 0
    var dy: Int = 0
    lateinit var bbox: Array<BBox>
    lateinit var child: Array<Int>
}

class Sector {
    var floorHeight: Int = 0
    var ceilingHeight: Int = 0
    lateinit var floorPic: String
    lateinit var ceilingPic: String
    var lightLevel: Int = 0
    var specialSector: Int = 0
    var tag: Int = 0
}

class Reject
class BlockMap
data class RGB(var r: Byte, val g: Byte, val b: Byte){}

class Palette {
    val table = Array<RGB>(255){RGB(0,0,0)}
}