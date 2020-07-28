package com.rndash.mbheadunit.canData.canB

import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class TVR_A2 : ECUFrame() {
    override val name : String = "TVR_A2"
    override val dlc: Int = 4
    override val id: Int = 0x0045

    override val signals: List<FrameSignal> = listOf(
            FrameSignal( "FHR_TVR", 8, 1),
            FrameSignal( "FHL_TVR", 9, 1),
            FrameSignal( "FVR_TVR", 10, 1),
            FrameSignal( "FVL_TVR", 11, 1),
            FrameSignal( "SHD_TVR", 12, 1),
            FrameSignal( "KB_RI_TVR", 13, 1),
            FrameSignal( "KB_MOD_TVR", 14, 1),
            FrameSignal( "FHR_AS_RL", 16, 1),
            FrameSignal( "FHR_MS_RL", 17, 1), // RR Close
            FrameSignal( "FHR_MOE_RL", 18, 1),// RR Open
            FrameSignal( "FHR_AOE_RL", 19, 1),
            FrameSignal( "FHL_AS_RL", 20, 1),
            FrameSignal( "FHL_MS_RL", 21, 1), // RL Close
            FrameSignal( "FHL_MOE_RL", 22, 1),// RL Open
            FrameSignal( "FHL_AOE_RL", 23, 1),
            FrameSignal( "FVL_AS", 28, 1),
            FrameSignal( "FVL_MS", 29, 1),
            FrameSignal( "FVL_MOE", 30, 1), // FL Close
            FrameSignal( "FVL_AOE", 31, 1)  // FL Open
    )

    override fun toString(): String {
        return """
            TVR_A2 (Window control module UK. Driver side)
        """.trimIndent()
    }

    fun setOpenFrontRight() {
        signals[19].setValue(0)
        signals[20].setValue(1)
    }

    fun setOpenRearRight() {
        signals[8].setValue(0)
        signals[9].setValue(1)
    }

    fun setOpenRearLeft() {
        signals[12].setValue(0)
        signals[13].setValue(1)
    }

    fun setCloseFrontRight() {
        signals[19].setValue(1)
        signals[20].setValue(0)
    }

    fun setCloseRearRight() {
        signals[8].setValue(1)
        signals[9].setValue(0)
    }

    fun setCloseRearLeft() {
        signals[12].setValue(1)
        signals[13].setValue(0)
    }

    fun setNeutralFrontRight() {
        signals[19].setValue(0)
        signals[20].setValue(0)
    }

    fun setNeutralRearRight() {
        signals[8].setValue(0)
        signals[9].setValue(0)
    }

    fun setNeutralRearLeft() {
        signals[12].setValue(0)
        signals[13].setValue(0)
    }
}