package com.rndash.mbheadunit.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.util.JsonReader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.BTMusic
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.nativeCan.KombiDisplay
import com.rndash.mbheadunit.partytime.*
import kotlinx.android.synthetic.main.lights_display.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.IndexOutOfBoundsException
import java.lang.Integer.max
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.collections.ArrayList


@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class LightsDisplay : Fragment() {
    var isInPage = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lights_display, container, false)
    }

    private var mapData: BeatSaberData? = null
    private lateinit var displayMatrix: Array<View>
    private var displayColours = Array(12){Color.TRANSPARENT}
    private lateinit var border: View
    private var bgColour = 0x00000000
    private val displayUpdater = object: TimerTask() {
        override fun run() {
            activity?.runOnUiThread {
                for (i in 0 until 12) {
                    displayMatrix[i].setBackgroundColor(displayColours[i])
                }
                border.setBackgroundColor(bgColour)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isInPage = true
        super.onViewCreated(view, savedInstanceState)
        PartyMode.startThread()

        val download_btn = view.findViewById<Button>(R.id.download_btn)
        val select_btn = view.findViewById<Button>(R.id.start_party_btn)
        border = view.findViewById(R.id.party_layout)
        displayMatrix = arrayOf(
            view.findViewById(R.id.b_0_0), view.findViewById(R.id.b_1_0), view.findViewById(R.id.b_2_0),
            view.findViewById(R.id.b_0_1), view.findViewById(R.id.b_1_1), view.findViewById(R.id.b_2_1),
            view.findViewById(R.id.b_0_2), view.findViewById(R.id.b_1_2), view.findViewById(R.id.b_2_2),
            view.findViewById(R.id.b_0_3), view.findViewById(R.id.b_1_3), view.findViewById(R.id.b_2_3),
        )

        download_btn.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://bsaber.com/songs/top")
            )
            startActivity(browserIntent)
        }

        select_btn.setOnClickListener {
            if (machineThread.isAlive) {
                Toast.makeText(requireContext(), "Song already playing", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(
                    Intent.createChooser(intent, "Select a BeatSaber ZIP file"),
                    1234
                )
            }
        }
    }

    private val machineThread = Thread() {
        val l = mapData?.levels!!.last()
        val media = mapData!!.af
        var lastTimeMs = 0
        val uiUpdater = Timer()
        var lastOnTimes=Array(12){0}
        uiUpdater.schedule(displayUpdater, 0, 10)
        while(true) {
            try {
                if (!media.isPlaying) {
                    break
                }
                val currTimeMs = media.currentPosition / 5
                if (currTimeMs != lastTimeMs) {
                    lastTimeMs = currTimeMs
                    val opList = l.instructions[currTimeMs]
                    if (opList != null) {
                        opList.forEach {
                            when(it.opcode) {
                                OpCode.DISPLAY_NOTE -> {
                                    displayColours[it.locy * 4 + it.locx] = it.c
                                    lastOnTimes[it.locy*4+it.locx] = currTimeMs
                                    if (it.locx < 2) {
                                        PartyMode.activateLeftBlinker(33)
                                    } else {
                                        PartyMode.activateRightBlinker(33)
                                    }
                                }
                                OpCode.BG_COLOUR_CHANGE -> {
                                    bgColour = it.c
                                    when(it.operand.toInt()) {
                                        1 -> PartyMode.activateDipped(300)
                                        2 -> PartyMode.activateFog(50)
                                        else -> {
                                            PartyMode.activateDipped(300)
                                            PartyMode.activateFog(0)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (i in 0 until 12) {
                        val tDelta = currTimeMs - lastOnTimes[i]
                        if (tDelta > 30) {
                            displayColours[i] = Color.TRANSPARENT
                        }
                    }
                } else {
                    Thread.sleep(1)
                }
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
                break // End of song
            }
        }
        uiUpdater.cancel()
        println("BS Thread bye")
        Thread.currentThread().interrupt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            val path = data!!.data!!
            if (!path.toString().endsWith(".zip")) {
                Toast.makeText(context, "Selected file is not a BeatSaber map", Toast.LENGTH_SHORT).show()
            }
            val input = requireContext().contentResolver.openInputStream(path)
            val zipstream = ZipInputStream(input)
            var entry: ZipEntry? = zipstream.nextEntry
            var info: String = ""
            try {
                File("/sdcard/mbux/bs_tmp/").deleteRecursively()
            } catch (e: Exception){}
            File("/sdcard/mbux/bs_tmp/").mkdirs()
            while(entry != null) {
                var f_name = entry.name.replace(".egg", ".ogg")
                val out = FileOutputStream("/sdcard/mbux/bs_tmp/"+f_name)
                val buffer = zipstream.readBytes()
                println("Reading ${entry.name} ${buffer.size} bytes")
                out.write(buffer)
                zipstream.closeEntry()
                out.close()
                entry = zipstream.nextEntry
            }
            val f = File("/sdcard/mbux/bs_tmp/info.dat");
            if (!f.exists()) {
                Toast.makeText(context, "Selected file is not a BeatSaber map", Toast.LENGTH_SHORT).show()
            }
            val json = JSONObject(f.readLines().joinToString(""))
            try {
                val audioFile = MediaPlayer.create(requireContext(), Uri.fromFile(File("/sdcard/mbux/bs_tmp/"+json.getString("_songFilename").replace(".egg", ".ogg"))))
                val levels = json.getJSONArray("_difficultyBeatmapSets")
                val initialBpm = json.getInt("_beatsPerMinute")
                // We are looking for the 'standard' Beatmap characteristics
                var standardLevels = JSONArray()
                for (i in 0 until levels.length()) {
                    val meta = JSONObject(levels[i].toString())
                    if (meta.getString("_beatmapCharacteristicName") == "Standard") {
                        standardLevels = meta.getJSONArray("_difficultyBeatmaps")
                        break
                    }
                }
                if (standardLevels.length() == 0) {
                    Toast.makeText(context, "Couldn't find any Standard BeatMaps", Toast.LENGTH_SHORT).show()
                    return
                }
                // now we have a list of standard levels, turn them into map data
                val allLevels = ArrayList<BeatSaberLevel>()
                for (i in 0 until standardLevels.length()) {
                    val levelMeta = JSONObject(standardLevels[i].toString())
                    val levelName = levelMeta.getString("_difficulty")
                    val levelFile = levelMeta.getString("_beatmapFilename")
                    try {
                        allLevels.add(
                            BeatSaberLevel(
                                levelName,
                                initialBpm,
                                JSONObject(
                                    File("/sdcard/mbux/bs_tmp/" + levelFile).readLines()
                                        .joinToString("")
                                )
                            )
                        )
                    } catch (e: BeatSaberLevel.LevelFailException){} // Don't worry
                }
                this.mapData = BeatSaberData(audioFile, allLevels)
                // Woohoo - start the party :D
                BTMusic.unfocusBT() // Let the headunit play music rather than BT
                KombiDisplay.setAudioHeaderText("Beatsaber", arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
                audioFile.start()
                machineThread.start()
                Toast.makeText(context, "The party has started!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error processing BS Map: ${e.cause}", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        isInPage = false
        PartyMode.stopThread()
    }

    override fun onResume() {
        super.onResume()
        isInPage = true
        PartyMode.startThread()
    }
}