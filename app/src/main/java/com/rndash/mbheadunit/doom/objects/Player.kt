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
        //sector = findSector(l.nodes.size - 1)
    }

    private fun rotate(angle: Double) {
        val oldxDir = xDir
        xDir = xDir * cos(angle) - yDir * sin(angle)
        yDir = oldxDir * sin(angle) + yDir * cos(angle)
        val oldxPlane = xPlane
        xPlane = xPlane * cos(angle) - yPlane * sin(angle)
        yPlane = oldxPlane * sin(angle) + yPlane * cos(angle)
    }
}