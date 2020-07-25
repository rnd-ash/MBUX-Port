package com.rndash.mbheadunit.canData.canB.KombiDisplay

@Suppress("UNUSED")
object ICDefines {
    enum class IC_PAGE(val byte: Byte) {
        AUDIO(0x03),
        TELE(0x05),
    }

    enum class IC_SYMBOL_AUDIO(val byte: Byte) {
        NONE(0x00),
        NEXT_TRACK(0x01),
        REV_TRACK(0x02),
        FAST_FWD(0x03),
        FAST_REW(0x04),
        PLAY(0x05),
        PLAY_REV(0x06),
        UP_ARROW(0x09),
        DOWN_ARROW(0x0A),
    }
}