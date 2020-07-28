package com.rndash.mbheadunit.canData.canB

import com.rndash.mbheadunit.canData.CanBusB
import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import com.rndash.mbheadunit.canData.UnVerifiedValue
import kotlin.math.ceil
import kotlin.math.roundToInt

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class THR_A1 : ECUFrame() {

    override val name: String = "THR_A1"
    override val dlc: Int = 2 // Full frame is 8, my unit only car 5 bytes??
    override val id: Int = 0x009C
    override val signals: List<FrameSignal> = listOf(
            FrameSignal("FHR_NORM", 0, 1),
            FrameSignal("FHR_BLOCK", 1, 1),
            FrameSignal("FHR_AUF", 2, 1),
            FrameSignal("FHR_KZHB", 3, 1),
            FrameSignal("FESTE_HR", 4, 12)
    )

    override fun toString(): String {
        return """
            THR_A1 (Rear right window control module)
            Window extension: ${getWindowPositionPercent()}%
            Window blocked?: ${isWindowBlocked()}
            Window open?: ${isWindowOpen()}
        """.trimIndent()
    }

    fun getWindowPositionPercent() : Int = CanBusB.getWindowOpenPercent(signals[4].getValue(), isWindowOpen())

    fun isWindowBlocked() : Boolean = signals[1].getValue() != 0
    fun isWindowOpen() : Boolean = signals[2].getValue() == 1

}