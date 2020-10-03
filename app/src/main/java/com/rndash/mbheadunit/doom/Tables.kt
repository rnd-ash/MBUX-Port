package com.rndash.mbheadunit.doom

import java.util.*

/**
 * Returns the name of the patch required
 * for the MIDI for each level in game
 *
 * Source: https://doom.fandom.com/wiki/Doom_music
 */
fun getMusicName(levelName: String): String {
    return when (levelName.toUpperCase(Locale.getDefault())) {
        "E1M1" -> "D_E1M1"
        "E1M2" -> "D_E1M2"
        "E1M3" -> "D_E1M3"
        "E1M4" -> "D_E1M4"
        "E1M5", "E4M4" -> "D_E1M5"
        "E1M6" -> "D_E1M6"
        "E3M6" -> "D_E3M6"
        "E1M7" -> "D_E1M7"
        "E2M5" -> "D_E2M5"
        "E3M5" -> "D_E3M5"
        "E4M8" -> "D_E4M8"
        "E1M8" -> "D_E1M8"
        "E3M4", "E4M1" -> "D_E3M4"
        "E1M9", "E4M9" -> "D_E1M9"
        "E3M9" -> "D_E3M9"
        "E2M1" -> "D_E2M1"
        "E2M2" -> "D_E2M2"
        "E3M3" -> "D_E2M3"
        else -> "D_INTER"
    }
}