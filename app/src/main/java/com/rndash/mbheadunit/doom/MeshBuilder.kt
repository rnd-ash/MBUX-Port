package com.rndash.mbheadunit.doom

import com.rndash.mbheadunit.doom.renderer.Vector3D
import com.rndash.mbheadunit.doom.wad.mapData.LineDef
import com.rndash.mbheadunit.doom.wad.mapData.Sector
import com.rndash.mbheadunit.doom.wad.mapData.Vertex

@ExperimentalUnsignedTypes
// v[first] = start vertex
// v[end] = end vertex
class MeshBuilder(private val isCeiling: Boolean, private val sector: Sector, private val vertexes: Array<Line>) {
    // Storage class
    class Line(var vStart: Vertex, var vEnd: Vertex) {
        override fun toString(): String {
            return "(${vStart.x},${vStart.y}) -> (${vEnd.x},${vEnd.y})"
        }
        fun swap() {
            val tmp = vStart
            vStart = vEnd
            vEnd = tmp
        }
    }

    inner class Shape() {

    }

    fun buildShape(start: ArrayList<Line>) {
        connectsTo(start.last()).let {
            it.firstOrNull { l -> l !in start }?.let { l ->
                start.add(l)
                buildShape(start)
            }
        }
    }

    fun toFloorMesh() : Mesh {
        val start = ArrayList<Line>()
        start.add(vertexes[0])
        buildShape(start)
        val vs = ArrayList<Vertex>().apply {
            vertexes.forEach { this.add(it.vStart); this.add(it.vEnd) }
            distinct()
        }
        var tmp = ArrayList<Vector3D>()
        val mxx = vs.maxByOrNull { it.x }!!.x
        val mix = vs.minByOrNull { it.x }!!.x
        val yxx = vs.maxByOrNull { it.y }!!.y
        val yix = vs.minByOrNull { it.y }!!.y
        val ty = (yxx - yix) / 64f // Texture x max
        val tx = (mxx - mix) / 64f // TExture y max

        val height = if (isCeiling) sector.ceilingHeight else sector.floorHeight
        if (vertexes.size == 4) {
            tmp.add(Vector3D(-mix, height, yxx, 0.0f, ty))
            tmp.add(Vector3D(-mix, height, yix, 0.0f, 0.0f))
            tmp.add(Vector3D(-mxx, height, yix, tx, 0.0f))

            tmp.add(Vector3D(-mxx, height, yix, tx, 0.0f))
            tmp.add(Vector3D(-mxx, height, yxx, tx, ty))
            tmp.add(Vector3D(-mix, height, yxx, 0.0f, ty))

        }

        println(start.joinToString(" connects to "))
        vertexes.forEach { l ->
            if (l !in start) {
                println("Warning. Found disjoined line")
            }
        }
        //tmp = vertexes.map { Vector3D(-it.vStart.x, sector.floorHeight, it.vStart.y, 0f, 1f) }
        return Mesh(*tmp.toTypedArray())
    }

    // Could be a disjoined line??
    // Checks to see if the END Vertex of the input matches another line's start or end
    fun connectsTo(input: Line) : List<Line> {
        return vertexes.filter {
            (it.vStart == input.vEnd || it.vStart == input.vStart || it.vEnd == input.vStart || it.vEnd == input.vEnd)
        }
    }
}