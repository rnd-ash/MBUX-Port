package com.rndash.mbheadunit

import com.rndash.mbheadunit.nativeCan.canC.MS_608h
import org.junit.Test
class FrameTest {
    @Test
    fun testECUSet() {
        var f : CanFrame = CanFrame(0x01D0, 'B', byteArrayOf(0x05,0x03,0x20, 0x02, 0x11, 0xC3.toByte(), 0x00, 0x00))
        println(f.toStruct().map {x -> String.format("%02X", x)}.joinToString(", "))
    }
}