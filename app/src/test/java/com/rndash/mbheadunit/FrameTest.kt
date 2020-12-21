package com.rndash.mbheadunit

import com.rndash.mbheadunit.nativeCan.canC.MS_608h
import org.junit.Test
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class FrameTest {
    @Test
    fun testECUSet() {
        var f : OldCanFrame = OldCanFrame(0x01D0, 'B', byteArrayOf(0x05,0x03,0x20, 0x02, 0x11, 0xC3.toByte(), 0x00, 0x00))
        var new_f = CanFrame(0x01D0, 'B', byteArrayOf(0x05,0x03,0x20, 0x02, 0x11, 0xC3.toByte(), 0x00, 0x00))

        println(f.getBitRange(0, 8))
        println(f.getBitRange(0, 3))
        println(new_f.getBitRange(0, 8))
        println(new_f.getBitRange(0, 3))

        val cfTime = measureNanoTime {
            OldCanFrame(0x01D0, 'B', byteArrayOf(0x05,0x03,0x20, 0x02, 0x11, 0xC3.toByte(), 0x00, 0x00)).run {
                getBitRange(0, 5)
                getBitRange(3, 10)
            }
        }

        val ncfTime = measureNanoTime {
            CanFrame(0x01D0, 'B', byteArrayOf(0x05,0x03,0x20, 0x02, 0x11, 0xC3.toByte(), 0x00, 0x00)).run {
                getBitRange(0, 5)
                getBitRange(3, 10)
            }
        }

        println("Times: $cfTime $ncfTime")
    }
}