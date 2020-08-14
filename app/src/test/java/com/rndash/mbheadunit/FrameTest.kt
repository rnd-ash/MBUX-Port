package com.rndash.mbheadunit

import org.junit.Test

class FrameTest {
    @Test
    fun checkConsistency() {
        val frame = CanFrame(0x0000, byteArrayOf(0x00, 0x00))
        println(frame)
        frame.setBitRange(0, 16, 0xF0FF)
        println(frame)
    }
}