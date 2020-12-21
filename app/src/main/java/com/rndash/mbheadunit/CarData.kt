package com.rndash.mbheadunit

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.nativeCan.canB.EZS_A11
import com.rndash.mbheadunit.nativeCan.canC.KOMBI_412h
import com.rndash.mbheadunit.nativeCan.canC.MS_608h
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
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
    private var executor = Executors.newScheduledThreadPool(1)
    init {
        println("CAR DATA INIT")
        executor.scheduleWithFixedDelay(
            {
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
            }, 0, 10, TimeUnit.MILLISECONDS
        )
    }

    fun registerCarTask(f: () -> Unit, timeDelayMs: Long) {
        println("CAR DATA REGISTER")
        executor.scheduleWithFixedDelay(f, 0, timeDelayMs, TimeUnit.MILLISECONDS)
    }
}