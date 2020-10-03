package com.rndash.mbheadunit.doom.objects

import android.R.attr
import com.rndash.mbheadunit.doom.wad.mapData.*
import kotlin.math.cos
import kotlin.math.sin


@ExperimentalUnsignedTypes
class Player(private val l: Level) {

    companion object {
        const val EYEHEIGHT = 56
    }

    private var xPos: Double = 0.0
    private var yPos: Double = 0.0
    private var zPos: Double = 0.0
    private var xDir: Double = -1.0
    private var yDir: Double = 0.0
    private var xPlane: Double = 0.0
    private var yPlane: Double = 0.66

    private var speed = 1.0
    private var rotSpeed = 1.0

    private var left = false
    private var right = false
    private var forward = false
    private var backward = false
    private var sector: Sector? = null

    fun setSpeed(spd: Double) {
        this.speed = spd
    }

    fun forwards(b: Boolean) {
        forward = true
        update()
        forward = false
    }

    fun backwards(b: Boolean) {
        backward = true
        update()
        backward = false
    }
    fun left(b: Boolean) {
        left = true
        update()
        left = false
    }
    fun right(b: Boolean) {
        right = true
        update()
        right = false
    }

    fun getXPos()  = xPos
    fun getYPos() = yPos
    fun getXDir() = xDir
    fun getYDir() = yDir
    fun getXPlane() = xPlane
    fun getYPlane() = yPlane

    fun setFloorHeight(floorHeight: Int) {
        zPos = (floorHeight + EYEHEIGHT).toDouble()
    }

    fun getX() = xPos.toInt()
    fun getY() = yPos.toInt()
    fun getSector(): Sector? = this.sector

    fun setPosition(x: Int, y: Int, rot: Int) {
        this.xPos = x.toDouble()
        this.yPos = y.toDouble()
        //val ang = Math.toRadians(rot.toDouble())
        //rotate(ang - rot.toDouble())
    }

    private fun segSideDef(seg: Seg, line: LineDef) : SideDef? {
        if (seg.segSide.toInt() == 0) {
            return l.sideDefs[line.sideDefRight.toInt()]
        } else {
            if (line.sideDefLeft.toInt() == -1) {
                return null
            }
            return l.sideDefs[line.sideDefLeft.toInt()]
        }
    }

    private fun segOppositeSideDef(seg: Seg, line: LineDef) : SideDef? {
        if (seg.segSide.toInt() == 0) {
            return l.sideDefs[line.sideDefRight.toInt()]
        } else {
            if (line.sideDefLeft.toInt() == -1) {
                return null
            }
            return l.sideDefs[line.sideDefLeft.toInt()]
        }
    }

    private fun intersects(x: Double, y: Double, box: Bbox): Boolean {
        return x.toInt() in (box.left .. box.right) && y.toInt() in (box.bottom .. box.top)
    }


    fun update() {
        if (forward) {
            xPos += xDir*speed
            yPos += yDir*speed
        }
        if (backward) {
            xPos -= xDir*speed
            yPos -= yDir*speed
        }
        if (right) {
            rotate(-rotSpeed)
        }
        if (left) {
            rotate(rotSpeed)
        }
        sector = findSector(l.nodes.size - 1)
    }

    private fun rotate(angle: Double) {
        val oldxDir = xDir
        xDir = xDir * cos(angle) - yDir * sin(angle)
        yDir = oldxDir * sin(angle) + yDir * cos(angle)
        val oldxPlane = xPlane
        xPlane = xPlane * cos(angle) - yPlane * sin(angle)
        yPlane = oldxPlane * sin(angle) + yPlane * cos(angle)
    }

    private fun findSector(id: Int) : Sector? {
        if (id and Scene.subsectorBit == Scene.subsectorBit) {
            val idx = (id.toUShort() and Scene.subsectorBit.toUShort().inv()).toInt()
            val ssector = l.subSectors[idx]
            for (segIdx in ssector.startSeg until ssector.startSeg + ssector.numSegs) {
                val seg = l.segs[segIdx]
                val linedef = l.lineDefs[seg.lineNum.toInt()]
                segSideDef(seg, linedef)?.let { return l.sectors[it.sectorRef.toInt()] }
                segOppositeSideDef(seg, linedef)?.let { return l.sectors[it.sectorRef.toInt()] }
            }

        }
        val node = l.nodes[id]
        if (intersects(xPos, yPos, node.bbox[0])) {
            return findSector(node.child[0].toInt())
        }
        if (intersects(xPos, yPos, node.bbox[1])) {
            return findSector(node.child[1].toInt())
        }
        return null
    }
}