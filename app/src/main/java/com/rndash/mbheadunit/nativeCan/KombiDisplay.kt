package com.rndash.mbheadunit.nativeCan

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


    external fun setPage(pg: Byte)
    fun setAudioBodyText(str: String, format: Array<TEXT_FMT>) {
        var fmt = 0
        format.forEach { f ->  fmt = fmt or f.raw }
        setBodyAttrs(0x03, fmt.toByte(), str)
    }

    fun setAudioHeaderText(str: String, format: Array<TEXT_FMT>) {
        var fmt = 0
        format.forEach { f ->  fmt = fmt or f.raw }
        setHeaderAttrs(0x03, fmt.toByte(), str)
    }


    private external fun setBodyAttrs(page: Byte, fmt: Byte, text: String)
    private external fun setHeaderAttrs(page: Byte, fmt: Byte, text: String);
}