package com.rndash.mbheadunit.doom.objects

import com.rndash.mbheadunit.doom.engine.FrameBuffer
import com.rndash.mbheadunit.doom.engine.View3D
import com.rndash.mbheadunit.doom.wad.structs.LevelData
import com.rndash.mbheadunit.doom.wad.structs.Vertex
import kotlin.math.tan

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class MapView(h: Int, private val level: LevelData): Drawable {
    val fov = 1.0 / tan(90.0 / 2.0) // degrees
    val viewPort = View3D(h)

    val maxX: Int
    val maxY: Int
    val minX: Int
    val minY: Int
    init {
        level.vertexes.map { it.Xcoord.toInt() }.run {
            maxX = this.maxOrNull()!! + 10
            minX = this.minOrNull()!! - 10
        }
        level.vertexes.map { it.YCoord.toInt() }.run {
            maxY = this.maxOrNull()!! + 10
            minY = this.minOrNull()!! - 10
        }
        println("X: $minX - $maxX Y: $minY - $maxY")
    }

    fun displayVertex(v: Vertex) {
        val x = ((v.Xcoord.toInt() - minX).toFloat() / (maxX - minX).toFloat()) * (320F)
        val y = ((v.YCoord.toInt() - minY).toFloat() / (maxY - minY).toFloat()) * (viewPort.viewPortHeight.toFloat())
        viewPort.setPixelColor(x.toInt(), y.toInt(), 0x10)
    }

    override fun render() {

        level.vertexes.forEach {
            displayVertex(it)
        }

    }

    override fun update() {

    }
}