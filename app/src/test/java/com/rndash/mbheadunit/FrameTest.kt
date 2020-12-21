package com.rndash.mbheadunit

import com.rndash.mbheadunit.nativeCan.canC.MS_608h
import org.junit.Test
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class FrameTest {
    @Test
    fun testECUSet() {
        var new_f = CanFrame(0x0230, 'B', byteArrayOf(0xC0.toByte(),0xFF.toByte()))

        var test_f = CanFrame(0x0230, 'B', byteArrayOf(0x00.toByte(),0x00.toByte()))
        test_f.setBitRange(0, 1, 1)
        test_f.setBitRange(1, 1, 1)
        test_f.setBitRange(8, 8, 0xFF)

        println(String.format("%02X %02X", new_f.getBitRange(0, 8), test_f.getBitRange(0, 8)))
        println(new_f.toStruct().joinToString(" ") { String.format("%02X", it) })
        println(test_f.toStruct().joinToString(" ") { String.format("%02X", it) })
    }
}