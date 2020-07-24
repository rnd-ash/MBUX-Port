package com.rndash.mbheadunit.canData.canC

import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import com.rndash.mbheadunit.canData.UnVerifiedValue

@ExperimentalUnsignedTypes
class GS418 : ECUFrame() {

    override val name : String = "GS418"
    override val dlc: Int = 8
    override val id: Int = 0x0418
    override val signals: List<FrameSignal> = listOf(
            FrameSignal( "FSC", 0, 8),
            FrameSignal( "FPC", 8, 8), // S = 83, C = 67, M = 77
            FrameSignal( "T_GET", 16, 8),
            FrameSignal( "ALLRAD", 24, 1),
            FrameSignal( "FRONT", 25, 1),
            FrameSignal( "SCHALT", 26, 1),
            FrameSignal( "CVT", 27, 1),
            FrameSignal( "MECH", 28, 2),
            FrameSignal( "ESV_BRE", 30, 1),
            FrameSignal( "KD", 31, 1),
            FrameSignal( "GZC", 32, 4),
            FrameSignal( "GIC", 36, 4),
            FrameSignal( "M_VERL", 40, 8),
            FrameSignal( "FMRADPAR", 48, 1),
            FrameSignal( "FMRADTGL", 49, 1),
            FrameSignal( "WHST", 50, 3),
            FrameSignal( "FMRAD", 53, 11)

    )

    override fun toString(): String {
        return """
            GS418 (722.x transmission)
            Oil temperature: ${getTransOilTemp()} Celsius
            Kick down?: ${isKickDown()}
            Target gear: ${getTargetGear()}
            Actual gear: ${getCurrentGear()}
        """
    }

    fun getTransOilTemp() : Int = signals[2].getValue() - 40
    fun isKickDown(): Boolean = signals[9].getValue() != 0
    fun getTargetGear() : Int = signals[10].getValue()
    fun getCurrentGear() : Int = signals[11].getValue()
    fun getShiftProgram() : Int = signals[1].getValue()
}