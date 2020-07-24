package com.rndash.mbheadunit.canData.canC

import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import java.lang.Integer.max
import java.lang.Integer.min

@ExperimentalUnsignedTypes
class MS608 : ECUFrame() {

    override val name : String = "MS608"
    override val dlc: Int = 8
    override val id: Int = 0x0608
    override val signals: List<FrameSignal> = listOf(
            FrameSignal( "T_MOT", 0, 8),
            FrameSignal("T_LUFT", 8, 8),
            FrameSignal( "FCOD_KAR", 16, 3),
            FrameSignal( "FCOD_BR", 19, 5),
            FrameSignal( "FCOD_MOT6", 24, 1),
            FrameSignal( "GS_NVH", 25, 1),
            FrameSignal( "FCOD_MOT", 26, 6),
            FrameSignal( "V_MAX_FIX", 32, 8),
            FrameSignal( "VB", 40, 16),
            FrameSignal( "ZWP_EIN_MS", 56, 1),
            FrameSignal( "PFW", 57, 2),
            FrameSignal( "ZVB_EIN_MS", 59, 1),
            FrameSignal( "PFKO", 60, 4)
    )

    override fun toString(): String {
        return """
            MS608 (Engine ECU)
            Engine temperature: ${getMotorTemp()} Celsius
            Max speed allowed (code): ${getVMax()} km/h
            Intake temperature: ${getIntakeTemp()} Celsius
            Current fuel consumption: ${getFuelConsumption()} ul/s
        """.trimIndent()
    }

    /**
     * Returns engine tempurature in Celcius
     */
    fun getMotorTemp() : Int = signals[0].getValue() - 40

    /**
     * Returns the MAX vehicle speed (Limited by ECU)
     * Unit is in Km/h
     */
    fun getVMax() : Int = signals[7].getValue()

    /**
     * Returns Intake tempurature in Celcius
     */
    fun getIntakeTemp() : Int = signals[1].getValue() - 40

    /**
     * Returns fuel consumed in ul/s
     */
    // Use Min here as sometimes when coasting we get a negative value!
    fun getFuelConsumption() : Int = max(0, signals[8].getValue())
}