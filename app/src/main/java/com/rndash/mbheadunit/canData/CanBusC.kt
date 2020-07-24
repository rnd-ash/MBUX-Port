package com.rndash.mbheadunit.canData

import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.canData.canC.*

@ExperimentalUnsignedTypes
object CanBusC  {
    /**
     * Gear enum varients for the 722.6 + 722.9 transmission
     */
    enum class Gear {
        P,
        R1,
        R2, // 722.x has 2 reverse gears!
        N,
        D1,
        D2,
        D3,
        D4,
        D5,
        D6,
        D7,
        UNKNOWN
    }

    enum class DriveProgram {
        SPORT,
        COMFORT,
        MANUAL,
        UNKNOWN
    }

    enum class CruiseState {
        ARMED,
        ON,
        OFF
    }


    private val ms608 = MS608()
    private val ms308 = MS308()
    private val gs418 = GS418()
    private val gs218 = GS218()

    fun updateFrames(incoming: CarCanFrame) {
        when(incoming.canID) {
            ms308.id -> ms308.parseFrame(incoming)
            ms608.id -> ms608.parseFrame(incoming)
            gs418.id -> gs418.parseFrame(incoming)
            gs218.id -> gs218.parseFrame(incoming)

        }
    }

    fun isEngineOn(): Boolean = ms308.getEngineRPM() != 0

    fun getTransmissionProgram() : DriveProgram {
        return getTransmissionProgram(gs418.getShiftProgram()).also { println("TRANS PROG $it") }
    }

    fun getGear() : Gear = getTransmissionGear(gs418.getTargetGear()).also { println("TRANS GEAR $it") }

    fun getCruiseState() : Pair<CruiseState, Int> {
        if (!isEngineOn()) {
            return Pair(CruiseState.OFF, 0)
        }
        return Pair(CruiseState.ARMED, 99)
    }

    fun getLimState() : Pair<CruiseState, Int> {
        if (!isEngineOn()) {
            return Pair(CruiseState.OFF, 0)
        }
        return Pair(CruiseState.ARMED, 99)
    }

    private fun getTransmissionGear(input: Int) : Gear {
        return when(input) {
            0 -> Gear.N
            11 -> Gear.R1
            12 -> Gear.R2
            13 -> Gear.P
            1 -> Gear.D1
            2 -> Gear.D2
            3 -> Gear.D3
            4 -> Gear.D4
            5 -> Gear.D5
            6 -> Gear.D6
            7 -> Gear.D7
            else -> Gear.UNKNOWN
        }
    }

    private fun getTransmissionProgram(input: Int) : DriveProgram {
        return when(input) {
            83 -> DriveProgram.SPORT
            67 -> DriveProgram.COMFORT
            77 -> DriveProgram.MANUAL
            else -> DriveProgram.UNKNOWN
        }
    }
}