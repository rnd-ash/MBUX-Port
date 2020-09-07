package com.rndash.mbheadunit.nativeCan

import android.util.Log
import com.rndash.mbheadunit.CanFrame
import com.rndash.mbheadunit.nativeCan.canB.CanBAddrs
import com.rndash.mbheadunit.nativeCan.canC.CanCAddrs

/**
 * Native Canbus wrapper for JNI Code
 */
object KombiDisplay {
    enum class TEXT_FMT(val raw: Int) {
        LEFT_JUSTIFIED(0x00),
        RIGHT_JUSTIFIED(0x08),
        CENTER_JUSTIFIED(0x10),
        FLASHING(0x20),
        HIGHLIGHTED(0x40)
    }

    enum class AUDIO_SYMBOL(val raw: Byte) {
        NONE(0x00),
        NEXT_TRACK(0x01),
        PREV_TRACK(0x02),
        FAST_FWD(0x03),
        FAST_REV(0x04),
        PLAY(0x05),
        REWIND(0x06),
        UP_ARROW(0x09),
        DOWN_ARROW(0x0A)
    }


    external fun setPage(pg: Byte)
    fun setAudioBodyText(str: String, format: Array<TEXT_FMT>) {
        Log.d("AGW-JVM", "Setting audio body to '$str'")
        var fmt = 0
        format.forEach { f ->  fmt = fmt or f.raw }
        setAudioBody(fmt.toByte(), str)
    }

    fun setTelBodyText(str1: String, format1: Array<TEXT_FMT>,
                       str2: String, format2: Array<TEXT_FMT>,
                       str3: String, format3: Array<TEXT_FMT>,
                       str4: String, format4: Array<TEXT_FMT>) {
        var fmt1 = 0
        var fmt2 = 0
        var fmt3 = 0
        var fmt4 = 0
        format1.forEach { f ->  fmt1 = fmt1 or f.raw }
        format2.forEach { f ->  fmt2 = fmt2 or f.raw }
        format3.forEach { f ->  fmt3 = fmt3 or f.raw }
        format4.forEach { f ->  fmt4 = fmt4 or f.raw }
        setTelBody(fmt1.toByte(), str1, fmt2.toByte(), str2, fmt3.toByte(), str3, fmt4.toByte(), str4 )
    }

    fun setAudioHeaderText(str: String, format: Array<TEXT_FMT>) {
        Log.d("AGW-JVM", "Setting audio header to '$str'")
        var fmt = 0
        format.forEach { f ->  fmt = fmt or f.raw }
        setHeaderAttrs(0x03, fmt.toByte(), str)
    }

    fun setTelHeaderText(str: String, format: Array<TEXT_FMT>) {
        Log.d("AGW-JVM", "Setting telephone header to '$str'")
        var fmt = 0
        format.forEach { f ->  fmt = fmt or f.raw }
        setHeaderAttrs(0x05, fmt.toByte(), str)
    }

    fun setAudioSymbol(upper: AUDIO_SYMBOL, lower: AUDIO_SYMBOL) = setAudioSymbolBytes(upper.raw, lower.raw)

    private external fun setHeaderAttrs(page: Byte, fmt: Byte, text: String)


    private external fun setAudioBody(fmt: Byte, text: String)
    private external fun setAudioSymbolBytes(u: Byte, d: Byte)

    private external fun setTelBody(fmt1: Byte, text1: String,
                                    fmt2: Byte, text2: String,
                                    fmt3: Byte, text3: String,
                                    fmt4: Byte, text4: String)
    private external fun setTelSymbolBytes(s1: Byte, s2: Byte, s3: Byte, s4: Byte)
}