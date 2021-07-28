package com.rndash.mbheadunit

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.nativeCan.canB.EZS_A11
import com.rndash.mbheadunit.nativeCan.canB.KOMBI_A1
import com.rndash.mbheadunit.nativeCan.canC.BS_200h
import com.rndash.mbheadunit.nativeCan.canC.KOMBI_412h
import com.rndash.mbheadunit.nativeCan.canC.MS_608h
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object CarData {
    const val WHEEL_CIRCUMFERENCE_CM = 199.136
    /// In km
    var tripDistance = 0.0
    // In kmh
    var currSpd = 0.0
    // In ul
    var tripFuelUsed = 0.0
    // In ul/sec
    var fuelCurrent = 0
    var isSportFeel: Boolean = false
    var batt_voltage = 0.0
    var isMetric = false // Mph or kmh?

    var show_esp_warn = false;

    fun get_speed(): Double {
        return when(isMetric) {
            true -> currSpd
            else -> currSpd * 5.0 / 8.0
        }
    }

    private var executor = Executors.newScheduledThreadPool(1)
    init {
        println("CAR DATA INIT")
        executor.scheduleAtFixedRate(
            {
                try {
                    show_esp_warn = BS_200h.get_esp_info_dl()
                    isMetric = KOMBI_A1.get_rr_km()
                    if (PartyMode.isEngineOn()) {
                        fuelCurrent = MS_608h.get_vb() // Converts to Ul / sec
                        tripFuelUsed += (fuelCurrent.toFloat() / 50.0) // 50th of a second

                        // new way of calculating speed, get front wheel RPM!
                        val avg_wheel_rpm = (BS_200h.get_dvl().toFloat()/2.0 + BS_200h.get_dvr().toFloat()/2.0) / 2.0
                        val m_per_sec = ((WHEEL_CIRCUMFERENCE_CM / 100.0) * avg_wheel_rpm) / 60.0
                        currSpd = m_per_sec * 3.6
                        tripDistance += currSpd / 180000.0 // Add speed (KMH)
                    } else {
                        currSpd = 0.0
                        fuelCurrent = 0
                    }
                    batt_voltage = EZS_A11.get_u_batt().toDouble() / 10.0
                } catch (e: Exception) {

                }
            }, 0, 20, TimeUnit.MILLISECONDS
        )
    }

    fun registerCarTask(f: () -> Unit, timeDelayMs: Long) {
        println("CAR DATA REGISTER")
        executor.scheduleAtFixedRate(f, 0, timeDelayMs, TimeUnit.MILLISECONDS)
    }
}