package com.rndash.mbheadunit.canData.canB

import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal

@ExperimentalUnsignedTypes
class SAM_V_A2 : ECUFrame() {
    override val name : String = "SAM_V_A2"
    override val dlc: Int = 6
    override val id: Int = 0x0017
    override val signals: List<FrameSignal> = listOf(
            FrameSignal( "T_AUSSEN_B", 0, 8),
            FrameSignal( "P_KAELTE", 8, 16),
            FrameSignal( "T_KAELTE", 24, 16),
            FrameSignal( "I_KOMP", 40, 8)
    )

    override fun toString(): String {
        return """
            SAM_V_A2
            Outside air temperature: ${getOutsideTemp()} Celsius
            Refrigerant Pressure: ${getRefrigerentPressure()} bar
            Refrigerant temperature: ${getRefrigerentTempurature()} Celsius
            Compressor control value: ${getCompressorCurrent()} mA
        """.trimIndent()
    }

    /**
     * Returns the outside air tempurature in Celsius, as measured from the sensor
     * on the front bumper (Beside the right fog lamp)
     */
    fun getOutsideTemp() : Double = signals[0].getValue().toFloat() / 2.0 - 40

    /**
     * Returns the pressure of the refrigerant within the AC Compressor in bars
     */
    fun getRefrigerentPressure() : Int = signals[1].getValue()

    /**
     * Returns the temperature of the refrigerant within the AC compressor in Celsius
     */
    fun getRefrigerentTempurature() : Int = signals[2].getValue()

    /**
     * Returns the current used by the AC compressor in milli-amps
     */
    fun getCompressorCurrent() : Int = signals[3].getValue()
}