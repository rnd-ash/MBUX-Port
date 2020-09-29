package com.rndash.mbheadunit.doom.objects

import com.rndash.mbheadunit.doom.SCREENHEIGHT
import com.rndash.mbheadunit.doom.SCREENWIDTH
import com.rndash.mbheadunit.doom.objects.StatusBar.Companion.ST_Y
import com.rndash.mbheadunit.doom.renderer.Renderer
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.mapData.*
import java.lang.Integer.max
import kotlin.math.cos
import kotlin.math.sin

@ExperimentalUnsignedTypes
class Scene(l: Level, private val wad: WadFile) {
    companion object {
        val subsectorBit = 0x8000
        const val RENDER_LIMIT = 1024
        const val SEG_LIMIT = 1024
        const val NEAR_Z = 1e-4
        const val FAR_Z = 5.0
        const val NEARSIDE = 1e-5
        const val FARSIDE = 20.0
        val VIEW_ANG: Double by lazy { Math.toRadians(180.0) }
        const val HFOV = SCREENHEIGHT * 0.73
        const val VFOV = SCREENHEIGHT / 2
    }
    val level = l
    val player = Player(level)

    // precache all flats and textures on load
    val flats = HashMap<String, Flat>()

    private var xMin = 0
    private var xMax = 0

    private var yMin = 0
    private var yMax = 0

    private var rat = 0


    init {
        level.things[0].let { player.setPos(it.xPos.toInt(), it.yPos.toInt()) }
        player.setAngle(level.things[0].angle.toInt())
        println("Player position (${player.getX()},${player.getY()}) at ${player.getAngleDegrees()} degrees")

        level.vertexes.forEach {
            if (it.x < xMin) { xMin = it.x.toInt() }
            if (it.x > xMax) { xMax = it.x.toInt() }

            if (it.y < yMin) { yMin = it.y.toInt() }
            if (it.y > yMax) { yMax = it.y.toInt() }
        }

        yMax += 10
        yMin -= 10

        xMax += 10
        xMin -= 10

        rat = max(kotlin.math.abs(yMax - yMin), kotlin.math.abs(xMax - xMin)) / ST_Y

        println("RR: $rat")
    }

    var renderCount = 0
    var segCount = 0
    fun traverse(id: Int, action: (Int) -> Unit) {
        if (renderCount > RENDER_LIMIT) { return }
        if (id and subsectorBit == subsectorBit) {
            return if (id == -1) {
                // First node in tree
                action(0)
            } else {
                renderCount++
                // Which node are we rendering?
                action((id.toUShort() and subsectorBit.toUShort().inv()).toInt())
            }
        }
        val n = level.nodes[id]
        val side = pointOnSide(n)
        val sideIdx = n.child[side].toInt()
        traverse(sideIdx, action)
        val oppSide = if (side == 1) 0 else 1
        val oppSideIdx = n.child[oppSide].toInt()
        traverse(oppSideIdx, action)
    }

    // Calculates which side the player is on
    // 0 - Front
    // 1 - Back
    private fun pointOnSide(node: Node): Int {
        val dx = player.getX() - node.x
        val dy = player.getY() - node.y
        val l = (node.dy.toInt() shr 16).toShort() * dx
        val r = (node.dx.toInt() shr 16).toShort() * dy
        return if (r < l) 0 else 1
    }

    private fun renderSector(s: SubSector) {
        if (segCount >= SEG_LIMIT) { return }
        (0 until s.numSegs).forEach { ns ->
            val seg = level.segs[ns + s.startSeg]
            // SegSide 0 = Front, 1 = back
            val line = level.lineDefs[seg.lineNum.toInt()]
            val c1 = level.vertexes[seg.vertexStart.toInt()]
            val c2 = level.vertexes[seg.vertexEnd.toInt()]
            line.let {
                val l1 = level.vertexes[it.vertexStart.toInt()]
                val l2 = level.vertexes[it.vertexEnd.toInt()]

                val vx1 = l1.x - player.getX()
                val vy1 = l1.y - player.getY()

                val vx2 = l2.x - player.getX()
                val vy2 = l2.y - player.getY()

                val psin = player.getSinAngle()
                val pcos = player.getCosAngle()

                val tx1: Double = vx1 * psin - vy1 * pcos
                val tz1: Double = vx1 * pcos + vy1 * psin
                val tx2: Double = vx2 * psin - vy2 * pcos
                val tz2: Double = vx2 * pcos + vy2 * psin

                if (tz1 <= 0 && tz2 <= 0) { return@forEach }
                drawMapLine(l1.x, l1.y, l2.x, l2.y, 0x03A)
                segCount++
            }
        }
        // Now draw the node
    }


    private fun getSideDef(seg: Seg, line: LineDef): SideDef? {
        if (seg.segSide.toInt() == 0) {
            return level.sideDefs[line.sideDefRight.toInt()]
        } else {
            if (line.sideDefLeft.toInt() == -1) { return null }
            return level.sideDefs[line.sideDefLeft.toInt()]
        }
    }

    private fun getOppositeSideDef(seg: Seg, line: LineDef): SideDef? {
        if (seg.segSide.toInt() == 0) {
            if (line.sideDefLeft.toInt() == -1) {
                return null
            }
            return level.sideDefs[line.sideDefLeft.toInt()]
        } else {
            return level.sideDefs[line.sideDefRight.toInt()]
        }
    }

    fun drawThing(x: Int, y: Int) {
        drawMapCoord(x, y, 0x6A)
    }

    fun drawMapCoord(x: Int, y: Int, col: Byte) {
        val newX = (SCREENWIDTH) * (x - xMin)/(xMax-xMin)
        val newY = (ST_Y) * (y - yMin)/(yMax-yMin)
        Renderer.drawPixel(newX, newY, col)
    }

    fun drawMapLine(x: Int, y: Int, x1: Int, y1: Int, col: Byte) {
        val newX = (SCREENWIDTH) * (x - xMin)/(xMax-xMin)
        val newY = (ST_Y) * (y - yMin)/(yMax-yMin)
        val newX1 = (SCREENWIDTH) * (x1 - xMin)/(xMax-xMin)
        val newY1 = (ST_Y) * (y1 - yMin)/(yMax-yMin)
        Renderer.drawLine(newX, newY, newX1, newY1, col)
    }

    fun drawMapLine(x: Short, y: Short, x1: Short, y1: Short, col: Byte) {
        val newX = (SCREENWIDTH) * (x - xMin)/(xMax-xMin)
        val newY = (ST_Y) * (y - yMin)/(yMax-yMin)
        val newX1 = (SCREENWIDTH) * (x1 - xMin)/(xMax-xMin)
        val newY1 = (ST_Y) * (y1 - yMin)/(yMax-yMin)
        Renderer.drawLine(newX, newY, newX1, newY1, col)
    }

    fun render() {
        renderCount = 0
        segCount = 0
        traverse(level.nodes.size - 1) {
            renderSector(level.subSectors[it])
        }
        //(1 until level.things.size).forEach {
        //    val t = level.things[it]
        //    drawThing(t.xPos.toInt(), t.yPos.toInt())
        //}

        drawMapCoord(player.getX(), player.getY(), 0x2F)
        drawMapLine(
                player.getX(),
                player.getY(),
                player.getX() + (1000 * cos(Math.toRadians(player.getAngleDegrees() - 30))).toInt(),
                player.getY() + (1000 * sin(Math.toRadians(player.getAngleDegrees() - 30))).toInt(),
                0xCA.toByte()
        )
        drawMapLine(
                player.getX(),
                player.getY(),
                player.getX() + (1000 * cos(Math.toRadians(player.getAngleDegrees() + 30))).toInt(),
                player.getY() + (1000 * sin(Math.toRadians(player.getAngleDegrees() + 30))).toInt(),
                0xCA.toByte()
        )
    }
}