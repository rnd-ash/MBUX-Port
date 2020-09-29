package com.rndash.mbheadunit.doom.objects

import com.rndash.mbheadunit.doom.SCREENHEIGHT
import com.rndash.mbheadunit.doom.renderer.Renderer
import com.rndash.mbheadunit.doom.wad.mapData.Level
import kotlin.math.cos
import kotlin.math.sin


class Player(l: Level) {
    companion object {
        const val EYEHEIGHT = 56
    }

    private var x: Double = 0.0 // Left / Right
    private var y: Double = 0.0 // Forwards/Backwards
    private var ang = 0.0
    private var sinAngle = 0.0
    private var cosAngle = 0.0
    private var z: Int = EYEHEIGHT

    private val DEGREES_90 : Double by lazy { Math.toRadians(90.0) }

    fun setFloorHeight(floorHeight: Int) {
        z = floorHeight + EYEHEIGHT
    }

    fun setAngle(x: Int) {
        ang = Math.toRadians(x.toDouble())
        sinAngle = sin(ang)
        cosAngle = cos(ang)
    }

    fun fwd(dist: Int) {
        x += cosAngle * dist
        y += sinAngle * dist
    }

    fun rev(dist: Int) {
        x -= cosAngle * dist
        y -= sinAngle * dist
    }

    fun getX() = x.toInt()
    fun getY() = y.toInt()

    fun setPos(x: Int, y: Int) {
        this.x = x.toDouble()
        this.y = y.toDouble()
    }

    fun left(dist: Int) {
        // TODO
    }

    fun right(dist: Int) {
        // TODO
    }

    fun getCosAngle() = cosAngle
    fun getSinAngle() = sinAngle

    fun getAngleDegrees() = Math.toDegrees(ang)

    fun getEyeHeight(): Int = z
}