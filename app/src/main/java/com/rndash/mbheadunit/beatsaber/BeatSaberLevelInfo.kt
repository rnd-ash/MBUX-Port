package com.rndash.mbheadunit.beatsaber

import com.rndash.mbheadunit.beatsaber.Block.Companion.NOTE_ACTION_DISTANCE
import com.rndash.mbheadunit.beatsaber.Block.Companion.NOTE_SPEED
import org.json.JSONObject
import java.io.File

/**
 * Stores data about a level listing within the game
 * @property difficulty - Difficulty or level name (Standard - Expert - Expert+ ... )
 * @property rank - Difficulty rank?
 * @property beatmapFile - File containing the level JSON
 * @property noteJumpMovementSpeed - Note jump movement speed
 * @property noteJumpStartOffset - Start offset for note jumping
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
data class BeatSaberLevelInfo(
    val difficulty: String,
    val rank: Int,
    val beatmapFile: File,
    val noteJumpMovementSpeed: Int,
    val noteJumpStartOffset: Int
) {
    private var msPerBeat = 1
    // Test - just returns all the blocks from the level
    fun processBlocks(bpm: Float) : ArrayList<Block> {
        val json = JSONObject(beatmapFile.readLines().joinToString(""))
        val blocks = ArrayList<Block>()
        val blockJson = json.getJSONArray("_notes")
        val msPerBeat = 60000f / bpm
        for (i in 0 until blockJson.length()) {
            val blockData = JSONObject(blockJson[i].toString())
            val xPos = blockData.getInt("_lineIndex")
            val yPos = blockData.getInt("_lineLayer")
            val zPos = ((msPerBeat * blockData.getDouble("_time").toFloat()) / NOTE_SPEED) + NOTE_ACTION_DISTANCE
            val type = blockData.getInt("_type")
            // Block rotation in degrees
            val angle = when(blockData.getInt("_cutDirection")) {
                0 -> 0   // up
                1 -> 180 // down
                2 -> 90  // left
                3 -> 270 // right
                4 -> 315 // up left
                5 -> 45  // up right
                6 -> 225 // down left
                7 -> 135 // down right
                else -> 0 // Any
            }.toFloat()
            when (type) {
                0 -> { // Red note
                    blocks.add(Block(xPos, yPos, zPos, angle, 1.0f, 0.0f, 0.0f, 1.0f, Block.Indicator.RIGHT))
                }
                1 -> { // Blue note
                    blocks.add(Block(xPos, yPos, zPos, angle, 0.0f, 0.0f, 1.0f, 1.0f, Block.Indicator.LEFT))
                }
                else -> {} // Bomb or unused - Ignore
            }
        }
        return blocks
    }

    private fun generateLightingEvent(targetLight: LightEvent.Light, value: Int, timestamp: Int): Array<LightEvent>? {
        return when(value) {
            0,5 -> { // Turns the light group off.
                arrayOf(LightEvent(targetLight, 0, timestamp)) // lights off
            }
            1,6 -> { // Changes the lights to blue/red, and turns the lights on.
                arrayOf(
                    LightEvent(targetLight, 0, timestamp-50),
                    LightEvent(targetLight, msPerBeat, timestamp),
                )
            }
            2,7 -> { // Changes the lights to blue/red, and flashes brightly before returning to normal.
                arrayOf(
                    LightEvent(targetLight, msPerBeat/2, timestamp), // Flash bright
                    //LightEvent(targetLight, 500, timestamp+1000), // Flash normal
                )
            }
            3 -> { // Changes the lights to blue/red, and flashes brightly before fading to black.
                arrayOf(
                    LightEvent(targetLight, 0, timestamp-50),
                    LightEvent(targetLight, msPerBeat*2, timestamp),
                )
            }
            else -> null // Unrecognised event
        }
    }


    fun processLights(bpm: Float, resolution: Int) : ArrayList<LightEvent> {
        val json = JSONObject(beatmapFile.readLines().joinToString(""))
        val lights = ArrayList<LightEvent>()
        val lightJson = json.getJSONArray("_events")

        msPerBeat = (60000.0 / bpm.toFloat()).toInt()
        // For lights, we will process it differently
        // Basically, store the time in ms (Rounded to resolution)
        // of which the event occurs, then the background thread will fire the lighting
        // events
        for (i in 0 until lightJson.length()) {
            val eventJson = JSONObject(lightJson[i].toString())
            val type = eventJson.getInt("_type") // Type of lighting event
            val timestamp = (((eventJson.getDouble("_time").toFloat() * msPerBeat) // Raw timestamp
                / resolution).toInt() * resolution) // Now this divison and multiplication will make it to the resolution
            val value = eventJson.getInt("_value")
            val event: Array<LightEvent>? = when(type) {
                0 -> generateLightingEvent(LightEvent.Light.FOG, value, timestamp) // Back laser
                1 -> generateLightingEvent(LightEvent.Light.FOG, value, timestamp) // Ring lights
                2 -> null //generateLightingEvent(LightEvent.Light.DIPPED, value, timestamp) // Left rotating laser
                3 -> null //generateLightingEvent(LightEvent.Light.DIPPED, value, timestamp) // Right rotating laser
                // Front LED's take about 0.1 seconds to actually activate...
                4 -> generateLightingEvent(LightEvent.Light.DIPPED, value, timestamp-100) // Center lights
                else -> null
            }
            event?.let { lights.addAll(it) }
        }
        // We probably have multiple lighting events at one timestamp, so sort unique
        // TODO
        println("${lights.size} lighting events")
        lights.sortBy { it.timestampMs } // Ensure that they are ordered!
        return lights
    }
}