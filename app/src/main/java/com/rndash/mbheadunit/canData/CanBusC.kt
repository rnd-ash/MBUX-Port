package com.rndash.mbheadunit.canData

import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.canData.canC.*
import kotlin.math.max
import kotlin.math.min

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
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


    val ms608 = MS608()
    val ms308 = MS308()
    val gs418 = GS418()
    val gs218 = GS218()
    val gs338 = GS338()

    fun updateFrames(incoming: CarCanFrame) {
        when(incoming.canID) {
            ms308.id -> ms308.parseFrame(incoming)
            ms608.id -> ms608.parseFrame(incoming)
            gs418.id -> gs418.parseFrame(incoming)
            gs218.id -> gs218.parseFrame(incoming)
            gs338.id -> gs338.parseFrame(incoming)
        }
    }

    fun isEngineOn(): Boolean = (ms308.getEngineRPM() != 0)

    fun getTransmissionProgram() : DriveProgram = getTransmissionProgram(gs418.getShiftProgram())

    fun getGear() : Gear = getTransmissionGear(gs418.getTargetGear())

    fun getCruiseState() : Pair<CruiseState, Int> {
        if (!isEngineOn()) {
            return Pair(CruiseState.OFF, 0)
        }
        return Pair(CruiseState.ARMED, 99)
    }

    private var fuel_cons_total: Long = 0
    fun getFuelConsumedTotal() : Long = fuel_cons_total


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

    // Keep track of MPG figures
    val fuelThread = Thread() {
        while(true) {
            this.fuel_cons_total += ms608.getFuelConsumption()
            Thread.sleep(1000)
        }
    }

    private var mpg = 0.0F
    private var lastMPGTime = System.currentTimeMillis()
    fun getMPG() : Float {
        if (System.currentTimeMillis() - lastMPGTime >= 1000) {
            lastMPGTime = System.currentTimeMillis()
            val spd = CanBusB.kombiA1.getSpeedKmh()
            if (spd == 0) {
                mpg = 0.0F
            } else if (ms608.getFuelConsumption() == 0) {
                mpg = 99.9F
            } else {
                val l_per_hour = 3600.0 * (ms608.getFuelConsumption() / 1000000.0)
                val km_l = spd / l_per_hour
                mpg = min(99.9, km_l * 2.82481).toFloat() // Don't exceed 99.9
            }

        }
        return mpg
    }

    /**
     * Returns an estimated percentage of the Torque converters
     * Lockup clutch duty cycle application percentage
     * in range 0 to 100.
     *
     * Known values:kon
     * Park or Neutral - Clutch is 0% lockup
     */
    fun getTCDuty() : Int {
        // Divide by 10 on both to get rid of any noise
        val engineRPM = (ms308.getEngineRPM() / 10)
        var tcRPM = (gs338.getTurbineSpeed() / 10) // Given at input to transmission

        // If in park or neutral, return 0 as TC Clutch is on 0% duty cycle
        if (gs218.getActualGear() == 0 || gs218.getActualGear() == 13) {
            return 0
        }
        // Now we know car must be in gear

        if (tcRPM == 0) {
            return 0
        }
        // Clamp TC RPM in case rounding error so output isn't > 100 %
        if (tcRPM > engineRPM) {
            tcRPM = engineRPM
        }
        return ((tcRPM.toDouble() / engineRPM.toDouble())*100).toInt()
    }

    init {
        fuelThread.start()
    }
}