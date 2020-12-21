package com.rndash.mbheadunit.beatsaber

import android.media.MediaPlayer
import java.io.File

/**
 * Contains info from info.dat about attributes on the map
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
data class Info(
    val songName: String,
    val songAuthor: String,
    val levelAuthor: String,
    val bpm: Float,
    val songFile: MediaPlayer,
    val coverImage: File) {
    val levels = ArrayList<BeatSaberLevelInfo>()

    fun addLevel(l: BeatSaberLevelInfo) {
        levels.add(l)
    }
}