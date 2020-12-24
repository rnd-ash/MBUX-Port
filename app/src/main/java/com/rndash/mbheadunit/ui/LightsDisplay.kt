package com.rndash.mbheadunit.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.rndash.mbheadunit.BeatSaberGLView
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.beatsaber.BeatSaberLevelInfo
import com.rndash.mbheadunit.beatsaber.GlView
import com.rndash.mbheadunit.beatsaber.Info
import com.rndash.mbheadunit.car.PartyMode
import kotlinx.android.synthetic.main.lights_display.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class LightsDisplay : UIFragment(0) {
    var isInPage = false

    companion object {
        var lightingEvents = 0
    }

    lateinit var beatsaberview: BeatSaberGLView
    lateinit var gameEngine: GlView
    lateinit var mapText: TextView
    lateinit var debugText: TextView
    lateinit var lightDisplay: Array<View> // UI showing car lights

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Create a simple OpenGl fragment here
        return inflater.inflate(R.layout.lights_display, container, false)
    }

    private val uiUpdater = object: TimerTask() {
        private fun setUIElement(on: Boolean, colour: Int, view: View) {
            if (on) {
                view.setBackgroundColor(colour)
            } else {
                view.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        override fun run() {
            activity?.runOnUiThread {
                // Indicators
                setUIElement(PartyMode.isLeftIndicatorOn(), Color.YELLOW, lightDisplay[0])
                setUIElement(PartyMode.isRightIndicatorOn(), Color.YELLOW, lightDisplay[1])

                setUIElement(PartyMode.isDippedOn(), Color.WHITE, lightDisplay[2])
                setUIElement(PartyMode.isDippedOn(), Color.WHITE, lightDisplay[3])

                setUIElement(PartyMode.isFogOn(), Color.WHITE, lightDisplay[4])
                setUIElement(PartyMode.isFogOn(), Color.WHITE, lightDisplay[5])
                debugText.text = "Debug (CAN TX)\nSAM_A_3: ${PartyMode.getSam3()}\nSAM_A_5: ${PartyMode.getSam5()}\nTotal exterior lighting events: $lightingEvents"
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectBeatmap = requireView().findViewById<Button>(R.id.select_beatmap_btn)
        beatsaberview = requireView().findViewById(R.id.bsview)
        gameEngine = beatsaberview.renderer

        lightDisplay = arrayOf(
            requireView().findViewById(R.id.indicator_left),
            requireView().findViewById(R.id.indicator_right),

            requireView().findViewById(R.id.dipped_left),
            requireView().findViewById(R.id.dipped_right),

            requireView().findViewById(R.id.fog_left),
            requireView().findViewById(R.id.fog_right),
        )
        mapText = requireView().findViewById(R.id.text_map)
        debugText = requireView().findViewById(R.id.beatsaber_debug)
        debugText.textSize = 11f

        selectBeatmap.setOnClickListener {
            when {
                PartyMode.isEngineOn() -> { // IMPORTANT - check if engine is running
                    Toast.makeText(requireContext(), "Engine is running!", Toast.LENGTH_LONG).show()
                }
                gameEngine.isLevelPlaying() -> { // Then check if a level is already playing
                    Toast.makeText(requireContext(), "Level is already playing!", Toast.LENGTH_LONG).show()
                }
                else -> { // We can load a level!
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "*/*" // Only want ZIP files
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    startActivityForResult(Intent.createChooser(intent, "Select a BeatSaber map zip"), 1234)
                }
            }
        }
    }

    override fun onKeyDown(code: Int, keyEvent: KeyEvent): Boolean {
        return beatsaberview.onKeyDown(code, keyEvent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            // User has selected a ZIP file, lets process it
            val path = data!!.data!! // This is the path of the selected file
            if (!path.toString().endsWith(".zip")) { // Do a sanity check!
                Toast.makeText(context, "Selected file is not a ZIP file!", Toast.LENGTH_SHORT).show()
                return
            }

            val input = requireContext().contentResolver.openInputStream(path)
            val zipStream = ZipInputStream(input)
            var entry: ZipEntry? = zipStream.nextEntry
            var info: String = ""

            try { // Delete our old temp directory
                File("/sdcard/mbux_tmp/").deleteRecursively()
            } catch (e: Exception){e.printStackTrace()}

            // Unzip the whole file into our temp directory
            while (entry != null) {
                // File name to extract - replace .egg with .ogg so android can play the sound file
                var fileName = entry.name.replace(".egg", ".ogg")
                try {
                    val out = FileOutputStream("/sdcard/mbux/bs_tmp/"+fileName)
                    out.write(zipStream.readBytes())
                    out.close()
                } catch (e: FileNotFoundException){e.printStackTrace()} // Can happen with autosave files, ignore
                zipStream.closeEntry()
                entry = zipStream.nextEntry
            }

            // Now locate info.dat that contains data about the map
            val f = File("/sdcard/mbux/bs_tmp/info.dat")
            if (!f.exists()) {
                // Couldn't find info.dat....maybe ZIP is not valid for the game?
                Toast.makeText(context, "Selected file is not a BeatSaber zip!", Toast.LENGTH_SHORT).show()
                return
            }
            val json = JSONObject(f.readLines().joinToString(""))
            try {
                // Load the metadata
                val info = Info(
                    json.getString("_songName"),
                    json.getString("_songAuthorName"),
                    json.getString("_levelAuthorName"),
                    json.getDouble("_beatsPerMinute").toFloat(),
                    MediaPlayer.create(requireContext(), Uri.fromFile(File("/sdcard/mbux/bs_tmp/"+json.getString("_songFilename").replace(".egg", ".ogg")))),
                    File("/sdcard/mbux/bs_tmp/cover.jpg")
                )

                val levelSets = json.getJSONArray("_difficultyBeatmapSets")
                println(levelSets.toString(1))
                // Now scan for standard levels only
                var stdLevels = JSONArray()
                for (i in 0 until levelSets.length()) {
                    val meta = JSONObject(levelSets[i].toString()) // Temp metadata
                    // Standard parsing, allow it
                    if (meta.getString("_beatmapCharacteristicName") == "Standard") {
                        // List of playable levels
                        val levelList = meta.getJSONArray("_difficultyBeatmaps")
                        for (x in 0 until levelList.length()) {
                            val levelMeta = JSONObject(levelList[x].toString())
                            try {
                                // Try and get the level data
                                info.addLevel(BeatSaberLevelInfo(
                                    levelMeta.getString("_difficulty"),
                                    levelMeta.getInt("_difficultyRank"),
                                    File("/sdcard/mbux/bs_tmp/"+levelMeta.getString("_beatmapFilename")),
                                    levelMeta.getInt("_noteJumpMovementSpeed"),
                                    levelMeta.getInt("_noteJumpStartBeatOffset")
                                ))
                            } catch (e: Exception) {
                                // Log this
                                e.printStackTrace()
                            }
                        }
                        break
                    }
                }
                when (info.levels.size) {
                    0 -> { // No levels :(
                        Toast.makeText(requireContext(), "No Standard BeatMaps found", Toast.LENGTH_LONG).show()
                        info.songFile.release() // MUST
                        return
                    }
                    1 -> { // 1 level, go straight to processing it
                        println("1 level found")
                        if (gameEngine.processChosenLevel(info, 0)) {
                            // Start the UI runnable that updates the light display
                            mapText.text = "Song: ${info.songName} by ${info.songAuthor}\nMapper: ${info.levelAuthor}"
                            Timer().schedule(uiUpdater, 0, 15)
                        }

                    }
                    else -> { // Ask the user which level they want to process
                        println("${info.levels.size} levels found")
                        showLevelMenu(info).show()
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Level processing failed", Toast.LENGTH_LONG).show()
                // TODO
            }
        }
    }

    private fun showLevelMenu(map: Info): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Choose a level")
        builder.setCancelable(false)
        builder.setItems(map.levels.map { it.difficulty }.toTypedArray()) { dialog, which ->
            Toast.makeText(requireContext(), "Processing ${map.levels[which].difficulty}", Toast.LENGTH_SHORT).show()
            if(gameEngine.processChosenLevel(map, which)) {
                mapText.text = "Song: ${map.songName} by ${map.songAuthor}\nMapper: ${map.levelAuthor}"
                Timer().schedule(uiUpdater, 0, 15)
            }
            dialog.cancel()
        }
        return builder.create()
    }


}