package com.rndash.mbheadunit.partytime

import android.graphics.Color
import org.json.JSONObject

class BeatSaberLevel(name: String, bpm: Int, json: JSONObject) {
    companion object {
        const val EVENT_OFFSET_MS = 5
        const val EVENT_RESOLUTION_MS = 5
    }
    inner class LevelFailException(cause: String) : Exception(cause){}

    lateinit var instructions: Array<MutableList<Instruction>?>
    init {
        println("Processing $name")
        val msPerBeat = (60000.0 / bpm.toFloat())
        val note_json = json.getJSONArray("_notes")
        val allevents = ArrayList<Pair<Int, Instruction>>()
        for (i in 0 until note_json.length()) {
            val note = JSONObject(note_json[i].toString())
            val beatTimeStampMs = note.getDouble("_time")
            val ts = (((beatTimeStampMs * msPerBeat).toLong() / EVENT_RESOLUTION_MS).toInt() - EVENT_OFFSET_MS)
            val lineIndex = note.getInt("_lineIndex")
            val lineLayer = note.getInt("_lineLayer")
            val type = note.getInt("_type")
            if (type == 0 || type == 1) {
                allevents.add(
                    Pair(
                        ts, Instruction(
                            OpCode.DISPLAY_NOTE, 10, lineIndex, lineLayer,
                            if (type == 0) {
                                Color.RED
                            } else {
                                Color.BLUE
                            }
                        )
                    )
                )
            }
        }

        val event_json = json.getJSONArray("_events")
        if (event_json.length() != 0) {
            for (i in 0 until event_json.length()) {
                val note = JSONObject(event_json[i].toString())
                val beatTimeStampMs = note.getDouble("_time")
                val type = note.getInt("_type")
                val value = note.getInt("_value")
                val ts = (((beatTimeStampMs * msPerBeat).toLong() / EVENT_RESOLUTION_MS).toInt() - EVENT_OFFSET_MS)
                var instruction: Instruction? = null
                when (type) {
                    0 -> { // Back center Lazer
                        var operand = 0L
                        val colour = when (value) {
                            1, 2, 3 -> {
                                operand = 1L
                                0x220000FF
                            } // Is it blue?
                            5, 6, 7 -> {
                                operand = 2L
                                0x22FF0000
                            } // Is it red?
                            else -> 0x00000000 // No colour
                        }
                        instruction = Instruction(OpCode.BG_COLOUR_CHANGE, operand, 0, 0, colour)
                    }
                    else -> {} // Unknown, do nothing
                }
                instruction?.let { allevents.add(Pair(ts, it)) }
            }
        }

        if (allevents.size == 0) {
            throw LevelFailException("BeatMap has no events")
        }

        val elementsNeeded = ((allevents.last().first * msPerBeat).toLong() / EVENT_RESOLUTION_MS)
        instructions = Array(elementsNeeded.toInt()){null}
        allevents.forEach { e ->
            if (instructions[e.first] == null) {
                instructions[e.first] = mutableListOf(e.second)
            } else {
                instructions[e.first]!!.add(e.second)
            }
        }
    }
}