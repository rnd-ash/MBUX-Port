package com.rndash.mbheadunit.canData.canB

import com.rndash.mbheadunit.canData.CanBusB
import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import kotlin.math.ceil

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class TVL_A3 : ECUFrame() {

    override val name: String = "TVL_A3"
    override val dlc: Int = 3
    override val id: Int = 0x0018
    override val signals: List<FrameSignal> = listOf(
            FrameSignal("SPVS_BF_RL", 0, 1),
            FrameSignal("HFE_RL", 1, 1),
            FrameSignal("KISI_EIN_RL", 2, 1),
            FrameSignal("ZBLR_DEF", 3, 1),
            FrameSignal("HFS_RL", 4, 11),
            FrameSignal("FVR_NORM", 8, 1),
            FrameSignal("FVR_BLOCK", 9, 1),
            FrameSignal("FVR_AUF", 10, 1),
            FrameSignal("FVR_KZHB", 11, 1),
            FrameSignal("FESTE_VR", 12, 12)
    )

    override fun toString(): String {
        return """
            TVL_A3 (Front left window control module)
            Window extension: ${getWindowPositionPercent()}%
            Window blocked?: ${isWindowBlocked()}
            Window open?: ${isWindowOpen()}
        """.trimIndent()
    }

    fun getWindowPositionPercent() : Int = CanBusB.getWindowOpenPercent(signals[9].getValue(), isWindowOpen())
    fun isWindowBlocked() : Boolean = signals[6].getValue() != 0
    fun isWindowOpen() : Boolean = signals[7].getValue() == 1

}