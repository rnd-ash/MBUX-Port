package com.rndash.mbheadunit

import com.rndash.mbheadunit.canData.DataFrame
import com.rndash.mbheadunit.canData.DataSignal
import org.junit.Test

@ExperimentalUnsignedTypes
class CustomFrame : DataFrame() {
    override val dlc: Int = 8

    override val id: Int = 0x0608

    override val name: String = "MS_608h"


    /**
     *
    MSG NAME: T_MOT - Engine coolant temperature, OFFSET 0, LENGTH 8
    MSG NAME: T_LUFT - intake, OFFSET 8, LENGTH 8
    MSG NAME: FCOD_KAR - Vehicle code body, OFFSET 16, LENGTH 3
    MSG NAME: FCOD_BR - Vehicle Code series, OFFSET 19, LENGTH 5
    MSG NAME: FCOD_MOT6 - Motor vehicle code with 7-bit, bit 6, OFFSET 24, LENGTH 1
    MSG NAME: GS_NVH - Transmission control No, OFFSET 25, LENGTH 1
    MSG NAME: FCOD_MOT - Fzgcod.Motor 7Bit, Bit0-5 (Bit6 -> Signal FCOD_MOT6), OFFSET 26, LENGTH 6
    MSG NAME: V_MAX_FIX - fixed speed, OFFSET 32, LENGTH 8
    MSG NAME: VB - consumption, OFFSET 40, LENGTH 16
    MSG NAME: ZWP_EIN_MS - Switch on auxiliary water pump, OFFSET 56, LENGTH 1
    MSG NAME: PFW - particulate filter warning, OFFSET 57, LENGTH 2
    MSG NAME: ZVB_EIN_MS - Turn on additional consumer, OFFSET 59, LENGTH 1
    MSG NAME: PFKO - Particulate filter correction offset FMMOTMAX, OFFSET 60, LENGTH 4
     */

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
}

class CustomFrame2 : DataFrame() {
    override val dlc: Int = 2

    override val id: Int = 0x0000

    override val name: String = "Test"

    override val signals: List<DataSignal> = listOf(
            DataSignal(0, "T_T1", 0, 16)
            //DataSignal(0, "T_T2", 3, 4)
    )
}

@ExperimentalUnsignedTypes
class CanTest {
    @Test
    fun test() {
        var x = CarCanFrame(0x0608, arrayOf(131,62,6,45,250,4,108,0))
        println(x)
        var f = CustomFrame()
        f.parseFrame(x)
        println(f)
        println(f.createCanFrame())
        println(f.signals[0].getValue())

    }

    @Test
    fun test2() {
        var x = CarCanFrame(0x0000, arrayOf<Int>(255,255))
        println(x)
        var f = CustomFrame2()
        f.parseFrame(x)
        println(f)
        println(f.createCanFrame())
        println(f.signals[0].getValue())
    }
}