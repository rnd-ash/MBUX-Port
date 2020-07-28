package com.rndash.mbheadunit

import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import com.rndash.mbheadunit.canData.canB.TVR_A2
import org.junit.Test

class TestFrame : ECUFrame() {

    override val name: String = "THL_A1"
    override val dlc: Int = 2 // Full frame is 8, my unit only car 5 bytes??
    override val id: Int = 0x0000
    override val signals: List<FrameSignal> = listOf(
            FrameSignal("FHR_NORM", 0, 1),
            FrameSignal("FHR_BLOCK", 1, 1),
            FrameSignal("FHR_AUF", 2, 1),
            FrameSignal("FHR_KZHB", 3, 1),
            FrameSignal("FESTE_HR", 4, 12)
    )
}

class FrameTest {
    @Test
    fun checkConsistency() {
        val raw = CarCanFrame(0x0000, arrayOf(0x2A, 0x80.toByte()))
        val tf = TestFrame()
        tf.parseFrame(raw)
        val tf2 = TestFrame()
        tf2.parseFrame(tf.createCanFrame())
        tf.signals.forEachIndexed { index, frameSignal ->
            assert(frameSignal.getValue() == tf2.signals[index].getValue())
        }
    }

    @ExperimentalStdlibApi
    @Test
    fun checkTVRA2() {
        val tvra2_frame = CarCanFrame(0x0045, arrayOf<Byte>(0x00,0x00,0x00,0x00))
        val tvra2 = TVR_A2()
        tvra2.parseFrame(tvra2_frame)
        tvra2.setCloseRearLeft()
        tvra2.setCloseRearRight()
        println(tvra2.toRawString())
        val tvra22 = TVR_A2()
        tvra22.parseFrame(tvra2.createCanFrame())
        println(tvra22.toRawString())
    }
}