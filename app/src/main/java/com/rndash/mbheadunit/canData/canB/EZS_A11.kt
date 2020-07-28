package com.rndash.mbheadunit.canData.canB

import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class EZS_A11 : ECUFrame() {
    override val name: String = "EZS_A11"
    override val dlc: Int = 1
    override val id: Int = 0x0016
    override val signals: List<FrameSignal> = listOf(
            FrameSignal("U_BATT", 0, 8)
    )

    override fun toString(): String {
        return """
            EZS_A11
            Battery Voltage: ${getBattVoltage()} Volts
        """.trimIndent()
    }

    fun getBattVoltage() : Double = signals[0].getValue().toDouble() / 10.0
}