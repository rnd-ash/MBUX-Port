package com.rndash.mbheadunit.canData.canB

import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import com.rndash.mbheadunit.canData.UnVerifiedValue

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class KLA_A2 : ECUFrame() {

    override val name: String = "KLA_A2"
    override val dlc: Int = 1 // Full frame is 8, my unit only car 5 bytes??
    override val id: Int = 0x0250
    override val signals: List<FrameSignal> = listOf(
            FrameSignal("FHR_KLA", 0, 1),
            FrameSignal("FHL_KLA", 1, 1),
            FrameSignal("FVR_KLA", 2, 1),
            FrameSignal("FVL_KLA", 3, 1),
            FrameSignal("SHD_KLA", 4, 1),
            FrameSignal("KB_RI_KLA", 5, 1),
            FrameSignal("KB_MOD_KLA", 6, 1)
    )

    override fun toString(): String {
        return """
            
        """.trimIndent()
    }
}