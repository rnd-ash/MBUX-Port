package com.rndash.mbheadunit.canData.canB.kombiDisplay

@Suppress("UNUSED")

/**
 * Contains defined ID's used when communication with cluster display
 */
object ICDefines {
    /**
     * Page ID defines when sending a payload to cluster.
     * This tells the cluster which page the payload is destined for
     */
    enum class Page(val byte: Byte) {
        AUDIO(0x03), // Audio page
        TELE(0x05),  // Telephone page
    }

    /**
     * Symbol ID's for audio page
     */
    enum class AudioSymbol(val byte: Byte) {
        NONE(0x00), // No Symbol
        NEXT_TRACK(0x01), // Next track |>>
        REV_TRACK(0x02), // Prev track <<|
        FAST_FWD(0x03), // Fast forward >>
        FAST_REW(0x04), // Fast rewind <<
        PLAY(0x05),     // Play >
        REWIND(0x06), // Rewind <
        UP_ARROW(0x09), // Up Arrow ^
        DOWN_ARROW(0x0A), // Down Arrow v
    }

    enum class TextFormat(val byte: Byte) {
        RIGHT_JUSTIFICATION(0x00),
        CENTER_JUSTIFICATION(0x10)
    }
}