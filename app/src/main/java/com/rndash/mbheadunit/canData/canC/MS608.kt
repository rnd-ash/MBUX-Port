package com.rndash.mbheadunit.canData.canC

import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.canData.DataFrame
import com.rndash.mbheadunit.canData.DataSignal
import java.lang.Integer.min
import kotlin.math.sign

@ExperimentalUnsignedTypes
class MS608 : DataFrame() {

    override val name : String = "MS608"
    override val dlc: Int = 8
    override val id: Int = 0x0608
    override val signals: List<DataSignal> = listOf(
            DataSignal(0, "T_MOT", 0, 8),
            DataSignal(0, "T_LUFT", 8, 8),
            DataSignal(0, "FCOD_KAR", 16, 3),
            DataSignal(0, "FCOD_BR", 19, 5),
            DataSignal(0, "FCOD_MOT6", 24, 1),
            DataSignal(0, "GS_NVH", 25, 1),
            DataSignal(0, "FCOD_MOT", 26, 6),
            DataSignal(0, "V_MAX_FIX", 32, 8),
            DataSignal(0, "VB", 40, 16),
            DataSignal(0, "ZWP_EIN_MS", 56, 1),
            DataSignal(0, "PFW", 57, 2),
            DataSignal(0, "ZVB_EIN_MS", 59, 1),
            DataSignal(0, "PFKO", 60, 4)
    )

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
    fun getFuelConsumption() : Int = min(0, signals[8].getValue())
}