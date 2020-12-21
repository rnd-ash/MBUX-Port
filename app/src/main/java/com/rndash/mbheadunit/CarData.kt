package com.rndash.mbheadunit

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.nativeCan.canB.EZS_A11
import com.rndash.mbheadunit.nativeCan.canC.KOMBI_412h
import com.rndash.mbheadunit.nativeCan.canC.MS_608h
import kotlin.properties.Delegates

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object CarData {
    var tripDistance = 0.0
    var currSpd = 0
    var tripFuelUsed = 0L
    var fuelCurrent = 0.0
    var isSportFeel: Boolean = false
    var batt_voltage = 0.0
    val dataCollector = Thread() {
        while(true) {
            try {
                if (PartyMode.isEngineOn()) {
                    fuelCurrent = MS_608h.get_vb() / 2.0 // Converts to Ul/s
                    tripFuelUsed += (fuelCurrent / 100).toLong() // 100th of a second
                    currSpd = KOMBI_412h.get_v_anz() // Current speed in Km/h
                    tripDistance += currSpd / 360000.0 // Add speed
                } else {
                    currSpd = 0
                    fuelCurrent = 0.0
                }
                batt_voltage = EZS_A11.get_u_batt().toDouble() / 10.0
                Thread.sleep(10L)
            } catch (e: Exception) {

            }
        }
    }
}