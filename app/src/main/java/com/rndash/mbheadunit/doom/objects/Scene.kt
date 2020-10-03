package com.rndash.mbheadunit.doom.objects

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.rndash.mbheadunit.doom.SCREENHEIGHT
import com.rndash.mbheadunit.doom.SCREENWIDTH
import com.rndash.mbheadunit.doom.getMusicName
import com.rndash.mbheadunit.doom.objects.StatusBar.Companion.ST_Y
import com.rndash.mbheadunit.doom.renderer.Renderer
import com.rndash.mbheadunit.doom.wad.WadFile
import com.rndash.mbheadunit.doom.wad.mapData.*
import java.lang.Integer.max
import kotlin.math.*

@ExperimentalUnsignedTypes
class Scene(l: Level, private val wad: WadFile, private val ctx: Context) {
    companion object {
        val subsectorBit = 0x8000
        const val RENDER_LIMIT = 1024
        val FOV = Math.toRadians(90.0)
        val VIEW_ANG: Double by lazy { Math.toRadians(180.0) }
        const val HFOV = SCREENHEIGHT * 0.73
        const val VFOV = SCREENHEIGHT / 2

        const val PLAYER_WIDTH = 32 // map units

        const val NEARZ = 1e-4
        const val FARZ = 5.0
        const val NEARSIDE = 1e-5
        const val FARSIDE = 20.0

    }
    val level = l
    val player = Player(level)

    // precache all flats and textures on load
    val flats = HashMap<String, Flat>()

    private var rat = 0

    init {
        level.things[0].let { player.setPosition(it.xPos.toInt(), it.yPos.toInt(), it.angle.toInt()) }
        level.cacheFlats(wad)
    }


    lateinit var mp: MediaPlayer
    fun startMusic() {
        try {
            val music = wad.getMidi(getMusicName(level.name))
            if (music) {
                mp = MediaPlayer.create(
                        ctx,
                        Uri.parse(
                                Environment.getExternalStorageDirectory().path + "/tmp.mid"
                        )
                )
                mp.isLooping = true
                mp.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(ctx, "Unable to find music for ${level.name}", Toast.LENGTH_LONG).show()
        }
    }

    var renderCount = 0
    var segCount = 0
    fun traverse(id: Int, x: Int, y: Int, action: (Int) -> Unit) {
        if (id and subsectorBit == subsectorBit) {
            return if (id == -1) {
                // First node in tree
                action(0)
            } else {
                // Which node are we rendering?
                action((id.toUShort() and subsectorBit.toUShort().inv()).toInt())
            }
        }
        val n = level.nodes[id]
        val side = pointOnSide(n, x, y)
        val sideIdx = n.child[side].toInt()
        traverse(sideIdx, x, y, action)
        val oppSide = side xor 1
        val oppSideIdx = n.child[oppSide].toInt()
        traverse(oppSideIdx, x, y, action)
    }

    // Calculates which side the player is on
    // 0 - Front
    // 1 - Back
    private fun pointOnSide(node: Node, x: Int, y: Int): Int {
        val dx = x - node.x
        val dy = y - node.y
        val l = (node.dy.toInt() shr 16) * dx
        val r = (node.dx.toInt() shr 16) * dy
        return if (r < l) 0 else 1
    }

    private fun renderSubSector(s: SubSector) {
        if (renderCount >= RENDER_LIMIT) { return }
        renderCount++
        val px = player.getX()
        val py = player.getY()
        for (ns in 0 until s.numSegs) {
            val seg = level.segs[ns + s.startSeg]
            // SegSide 0 = Front, 1 = back
            val line = level.lineDefs[seg.lineNum.toInt()]
            val c1 = level.vertexes[line.vertexStart.toInt()]
            val c2 = level.vertexes[line.vertexEnd.toInt()]

        }
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

    private fun clipHorizontal(x: Int): Int = if (x >= SCREENWIDTH) { SCREENWIDTH - 1 } else if (x < 0) { 0 } else x
    private fun clipVertical(y: Int): Int = if (y >= SCREENHEIGHT) { SCREENHEIGHT - 1 } else if (y < 0) { 0 } else y


    fun render() {
        renderCount = 0
        //traverse(level.nodes.size-1, player.getX(), player.getY()) {
        //    renderSubSector(level.subSectors[it])
        //}
        player.getSector()?.let {
            Renderer.setSky(level.flats[it.ceilingPic]!![0])
            Renderer.setFloor(level.flats[it.floorPic]!![0])
        }

    }
}