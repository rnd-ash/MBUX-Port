package com.rndash.mbheadunit.doom.wad.structs

import com.rndash.mbheadunit.doom.wad.Int16
import com.rndash.mbheadunit.doom.wad.UInt16
import org.w3c.dom.Text

@ExperimentalUnsignedTypes
class LevelData(val name: String) {
    lateinit var things: Array<Thing>
    lateinit var lineDefs: Array<LineDef>
    lateinit var sideDefs: Array<SideDef>
    lateinit var vertexes: Array<Vertex>
    lateinit var segs: Array<Seg>
    lateinit var ssectors: Array<SSector>
    lateinit var nodes: Array<Node>
    lateinit var sectors: Array<Sector>
}

@ExperimentalUnsignedTypes
data class Thing(val x_pos: Int16, val y_pos: Int16, val angle: UInt16, val type: UInt16, val flags: UInt16) : Struct {
    companion object {
        const val FLAG_EASY: Int16 = 0x0001 // Thing is on skill level 1-2
        const val FLAG_MEDIUM: Int16 = 0x0002   // Thing is on skill level 3
        const val FLAG_HARD: Int16 = 0x0004 // Thing is on skill level 4-5
        const val FLAG_AMBUSH: Int16 = 0x0008  // Thing is deaf
        const val FLAG_DORMANT: Int16 = 0x0010 // Thing is NOT in single player mode
    }
}

@ExperimentalUnsignedTypes
class LineDef(val vStart: Int16, val vEnd: Int16, val flags: Int16, val func: Int16, val tag: Int16, val sdr: Int16, val sdl: Int16): Struct

@ExperimentalUnsignedTypes
class SideDef constructor(
        val xOffset: Int16,
        val yOffset: Int16,
        val upperTex: String,
        val midText: String,
        val lowText: String,
        val sectorRef: UInt16) : Struct

@ExperimentalUnsignedTypes
class Vertex(var Xcoord: Int16, var YCoord: Int16) : Struct

@ExperimentalUnsignedTypes
class Seg(
        val startVertex: Int16,
        val endVertex: Int16,
        val angle: Int16,
        val lindef: UInt16,
        val segSide: Int16,
        val offset: Int16) : Struct

@ExperimentalUnsignedTypes
class SSector(val numSegs: Int16, val startSeg: Int16) : Struct

@ExperimentalUnsignedTypes
class Node(
        var X: Int16, var Y: Int16, var dx: Int16, var dy: Int16
) : Struct {
    lateinit var bbox: Array<BBox> // 2 in size
    lateinit var child: Array<Int16> // 2 in size
}

@ExperimentalUnsignedTypes
class BBox(val top: Int16, val bottom: Int16, val left: Int16, val right: Int16): Struct

@ExperimentalUnsignedTypes
class Sector(
        val floorHeight: Int16,
        val ceilingHeight: Int16,
        val floorPic: String,
        val ceilingPic: String,
        val lightLevel: Int16,
        val specialSector: UInt16,
        val tag: UInt16
) : Struct