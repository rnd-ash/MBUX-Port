package com.rndash.mbheadunit.partytime

import android.graphics.Color

class BeatSaberNote(val beatTimestamp: Double, val lineIndex: Int, val lineLayer: Int, val cutDir: Int, val type: Int) {
    override fun toString(): String {
        return "[TS: $beatTimestamp - LOC: (${lineIndex}x${lineLayer}) - DIR: $cutDir - TYPE: $type]"
    }
}