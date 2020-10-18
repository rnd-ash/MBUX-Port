package com.rndash.mbheadunit.doom

import com.rndash.mbheadunit.doom.renderer.ColourMap
import com.rndash.mbheadunit.doom.renderer.Vector3D
import com.rndash.mbheadunit.doom.things.AbstractThing
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.mapData.*

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class GLMap(private val l: Level, private val w: WadFile, private val cMap: Array<ColourMap>) {
    companion object {
        const val SUBSECTOR_BIT = 0x8000
    }

    val lines = l.lineDefs.toList().distinct()

    val renderMeshes = ArrayList<Mesh>()

    fun findSector(x: Int, y: Int): Pair<Sector, Short>? {
        return findSector(x, y, l.nodes.size-1)
    }

    fun findSector(x: Int, y: Int, id: Int): Pair<Sector, Short>? {
        if (id and SUBSECTOR_BIT == SUBSECTOR_BIT) {
            val idx = (id.toUShort() and SUBSECTOR_BIT.toUShort().inv()).toInt()
            val ssector = l.subSectors[idx]
            for (segIdx in ssector.startSeg until ssector.startSeg + ssector.numSegs) {
                val seg = l.segs[segIdx]
                val line = l.lineDefs[seg.lineNum.toInt()]
                segSideDef(seg, line)?.let { return Pair(l.sectors[it.sectorRef.toInt()],it.sectorRef) }
                segOppositeSideDef(seg, line)?.let { return Pair(l.sectors[it.sectorRef.toInt()],it.sectorRef) }
            }
        }
        val node = l.nodes[id]
        if (intersects(x, y, node.bbox[0])) {
            return findSector(x, y, node.child[0].toInt())
        }
        if (intersects(x, y, node.bbox[1])) {
            return findSector(x, y, node.child[1].toInt())
        }
        return null
    }

    private fun intersects(x: Int, y: Int, bbox: Bbox): Boolean {
        return x in (bbox.left until bbox.right) && y in (bbox.bottom until bbox.top)
    }

    fun genMap() {
        val sectorMeshes = ArrayList<MeshTemp>() // Walls
        val topBottomMeshes = ArrayList<MeshTemp>() // Floors and ceilings
        l.subSectors.forEach {
            (it.startSeg until it.startSeg + it.numSegs).forEach { i ->
                sectorMeshes.addAll(genSeg(i))
            }
            buildSsectorFlats(it, false)?.let { m -> topBottomMeshes.add(m) }
            buildSsectorFlats(it, true)?.let { m -> topBottomMeshes.add(m) }
        }
        val comb = sectorMeshes.groupBy { m -> m.tex }
        val combf = topBottomMeshes.groupBy { m -> m.tex }
        println("Combining ${sectorMeshes.size} Meshes into ${comb.size} meshes")
        comb.forEach {
            renderMeshes.add(
                    Mesh(
                            *it.value.map { it.points }.flatten().toTypedArray()
                    ).apply { cacheTexture(it.key, w, cMap, 100) }
            )
        }
        combf.forEach {
            renderMeshes.add(
                    Mesh(
                            *it.value.map { it.points }.flatten().toTypedArray()
                    ).apply { cacheFlat(it.key, w, cMap, 100) }
            )
        }
        genThings()
    }

    fun genThings() {
        l.things.forEach {
            /*
            things.add(AbstractThing(it, this).apply { genSprite(w,
                    when(it.type.toInt()){
                        68 -> "BSPI"
                        64 -> "VILE"
                        3003 -> "BOSS"
                        3004 -> "POSS"
                        3005 -> "HEAD"
                        else -> return@forEach
                    }, cMap[0]) })

             */
        }
    }

    //private var meshes = arrayListOf<Mesh>()
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
            if (line.sideDefLeft.toInt() == -1) {
                return null
            }
            return l.sideDefs[line.sideDefLeft.toInt()]
        } else {
            return l.sideDefs[line.sideDefLeft.toInt()]
        }
    }

    private fun buildSsectorFlats(ss: SubSector, ceiling: Boolean): MeshTemp? {
        val sampleSeg = l.segs[ss.startSeg.toInt()]
        val sampleLine = l.lineDefs[sampleSeg.lineNum.toInt()]
        val sideDef = segSideDef(sampleSeg, sampleLine) ?: return null
        val sector = l.sectors[sideDef.sectorRef.toInt()]
        val lines = (ss.startSeg until ss.startSeg+ss.numSegs)
                .map { l.segs[it] }
                .map { l.lineDefs[it.lineNum.toInt()] }
        println("${lines.size} lines in SS")
        val points =  lines
                .map { listOf(it.vertexStart, it.vertexEnd) }
                .flatten()
                .distinct()
                .map { l.vertexes[it.toInt()] }
                .sortedBy { it.x }

        if (points.size == 4) {
            println("Cube")
            val texWidth = points.map { it.x }.sorted().let { it.last() - it.first() } / 64f
            val texHeight = points.map { it.y }.sorted().let { it.last() - it.first() } / 64f
            val top = points.sortedBy { it.y }.take(2).sortedBy { it.x }
            val bottom = points.sortedBy { it.y }.takeLast(2).sortedBy { it.x }
            val height = if (ceiling) { sector.ceilingHeight } else sector.floorHeight
            return MeshTemp(
                    if (ceiling) { sector.ceilingPic } else sector.floorPic,
                    Vector3D(-bottom[0].x, height, bottom[0].y, 0.0f, 0.0f),
                    Vector3D(-top[0].x, height, top[0].y, 0.0f, texHeight),
                    Vector3D(-top[1].x, height, top[1].y, texWidth, texHeight),

                    Vector3D(-top[1].x, height, top[1].y, texWidth, texHeight),
                    Vector3D(-bottom[1].x, height, bottom[1].y, texWidth, 0.0f),
                    Vector3D(-bottom[0].x, height, bottom[0].y, 0.0f, 0.0f)
            )
        }
        if (points.size == 3) {
            val minY = points.sortedBy { it.y }[0]
            val minX = points.sortedBy { it.x }.first { it != minY }
            val third = points.first { it != minY && it != minX }
            return MeshTemp(
                    sector.floorPic,
                    Vector3D(-minX.x.toFloat(),  sector.floorHeight.toFloat(), minX.y.toFloat(), 1f, 0f),
                    Vector3D(-minY.x.toFloat(),  sector.floorHeight.toFloat(), minY.y.toFloat(), 0f, 0f),
                    Vector3D(-third.x.toFloat(),  sector.floorHeight.toFloat(), third.y.toFloat(), 1f, 1f),
            )
        }
        return null
    }

    inner class MeshTemp(val tex: String, vararg vecs: Vector3D) {
        val points = vecs.toList()
    }

    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    private fun genSeg(segID: Int) : ArrayList<MeshTemp> {
        val meshes = ArrayList<MeshTemp>()
        val seg = l.segs[segID]
        val lineDef = l.lineDefs[seg.lineNum.toInt()]
        val sideDef = segSideDef(seg, lineDef) ?: return meshes

        val sector = l.sectors[sideDef.sectorRef.toInt()]
        val oppSideDef = segOppositeSideDef(seg, lineDef)

        val ut = sideDef.uppperTexture
        val mt = sideDef.middleTexture
        val lt = sideDef.lowerTexture

        val start = l.vertexes[seg.vertexStart.toInt()]
        val end = l.vertexes[seg.vertexEnd.toInt()]


        if (ut != "-" && oppSideDef != null) {
            val oppSector = l.sectors[oppSideDef.sectorRef.toInt()]
            meshes.add(
                    MeshTemp(
                            ut,
                            Vector3D(-start.x, sector.ceilingHeight, start.y, 0.0f, 0.0f),
                            Vector3D(-start.x, oppSector.ceilingHeight, start.y, 0.0f, 1.0f),
                            Vector3D(-end.x, oppSector.ceilingHeight, end.y, 1.0f, 1.0f),

                            Vector3D(-end.x, oppSector.ceilingHeight, end.y, 1.0f, 1.0f),
                            Vector3D(-end.x, sector.ceilingHeight, end.y, 1.0f, 0.0f),
                            Vector3D(-start.x, sector.ceilingHeight, start.y, 0.0f, 0.0f)
                    )
            )
        }
        if (mt != "-") {
            meshes.add(
                    MeshTemp(
                            mt,
                            Vector3D(-start.x, sector.floorHeight, start.y, 0.0f, 1.0f),
                            Vector3D(-start.x, sector.ceilingHeight, start.y, 0.0f, 0.0f),
                            Vector3D(-end.x, sector.ceilingHeight, end.y, 1.0f, 0.0f),

                            Vector3D(-end.x, sector.ceilingHeight, end.y, 1.0f, 0.0f),
                            Vector3D(-end.x, sector.floorHeight, end.y, 1.0f, 1.0f),
                            Vector3D(-start.x, sector.floorHeight, start.y, 0.0f, 1.0f)
                    )
            )
        }
        if (lt != "-" && oppSideDef != null) {
            val oppSector = l.sectors[oppSideDef.sectorRef.toInt()]
            meshes.add(
                    MeshTemp(
                            lt,
                            Vector3D(-start.x, sector.floorHeight, start.y, 0.0f, 1.0f),
                            Vector3D(-start.x, oppSector.floorHeight, start.y, 0.0f, 0.0f),
                            Vector3D(-end.x, oppSector.floorHeight, end.y, 1.0f, 0.0f),

                            Vector3D(-end.x, oppSector.floorHeight, end.y, 1.0f, 0.0f),
                            Vector3D(-end.x, sector.floorHeight, end.y, 1.0f, 1.0f),
                            Vector3D(-start.x, sector.floorHeight, start.y, 0.0f, 1.0f)
                    )
            )
        }
        return meshes
    }

    private var things = ArrayList<AbstractThing>()



    fun render(v: FloatArray, f: FloatArray, px: Float, py: Float) {
        renderMeshes.forEach { it.draw(v, f) }
        var renderCount = 0
        for (thing in things) {
            if (renderCount < 256 && thing.xPos in (px-750..px+750) && thing.zPos in (py-750..py+750)) {
                thing.render(v, f)
                renderCount++
            }
        }
    }
}