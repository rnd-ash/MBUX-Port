package com.rndash.mbheadunit

import com.rndash.mbheadunit.nativeCan.canC.MS_608h
import org.junit.Test
class FrameTest {
    @Test
    fun checkConsistency() {
        val frame = CanFrame(0x0000, 'B', byteArrayOf(0x00, 0x00))
        println(frame)
        frame.setBitRange(0, 16, 0xF0FF)
        println(frame)
    }

    @Test
    fun testECUSet() {
        var f : CanFrame? = CanFrame(0x608, 'B', 8)
        f = MS_608h.set_t_mot(f, 111)
        println(f)

        // Target content 6F 43 06 2D FA 00 36 00
        f?.let {
            assert(it.data.array().contentEquals(byteArrayOf(0x6F, 0x43, 0x06, 0x2D, 0xFA.toByte(), 0x00, 0x36, 0x00)))
        }

    }
}