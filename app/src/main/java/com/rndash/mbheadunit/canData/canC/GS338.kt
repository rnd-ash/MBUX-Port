package com.rndash.mbheadunit.canData.canC

import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import com.rndash.mbheadunit.canData.UnVerifiedValue

@ExperimentalUnsignedTypes
class GS338 : ECUFrame() {

    override val name : String = "GS338"
    override val dlc: Int = 8
    override val id: Int = 0x0338
    override val signals: List<FrameSignal> = listOf(
            FrameSignal( "NAB", 0, 16),
            FrameSignal( "TURBINE", 48, 16)
    )

    override fun toString(): String {
        return """
            GS338 (722.x transmission)
            Output shaft speed: ${getOutputSpeed()} RPM
            Turbine speed: ${getTurbineSpeed()} RPM
        """
    }

    fun getOutputSpeed() : Int = signals[0].getValue()
    fun getTurbineSpeed(): Int = signals[1].getValue()
}