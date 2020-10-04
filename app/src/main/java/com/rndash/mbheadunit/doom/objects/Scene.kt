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