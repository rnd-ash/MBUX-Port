package com.rndash.mbheadunit.canData.canB

import com.rndash.mbheadunit.canData.CanBusB
import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import com.rndash.mbheadunit.canData.UnVerifiedValue
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.ceil

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class THL_A1 : ECUFrame() {

    override val name: String = "THL_A1"
    override val dlc: Int = 2 // Full frame is 8, my unit only car 5 bytes??
    override val id: Int = 0x009A
    override val signals: List<FrameSignal> = listOf(
            FrameSignal("FHR_NORM", 0, 1),
            FrameSignal("FHR_BLOCK", 1, 1),
            FrameSignal("FHR_AUF", 2, 1),
            FrameSignal("FHR_KZHB", 3, 1),
            FrameSignal("FESTE_HR", 4, 12)
    )

    override fun toString(): String {
        return """
            THL_A1 (Rear left window control module)
            Window extension: ${getWindowPositionPercent()}%
            Window blocked?: ${isWindowBlocked()}
            Window open?: ${isWindowOpen()}
        """.trimIndent()
    }

    fun getWindowPositionPercent() : Int = CanBusB.getWindowOpenPercent(signals[4].getValue(), isWindowOpen())
    fun isWindowBlocked() : Boolean = signals[1].getValue() != 0
    fun isWindowOpen() : Boolean = signals[2].getValue() == 1

}