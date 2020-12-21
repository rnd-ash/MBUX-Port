package com.rndash.mbheadunit.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.CarData
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.nativeCan.canB.KOMBI_A1
import com.rndash.mbheadunit.nativeCan.canB.KOMBI_A5
import com.rndash.mbheadunit.nativeCan.canB.SAM_H_A2
import com.rndash.mbheadunit.nativeCan.canB.SAM_V_A2
import com.rndash.mbheadunit.nativeCan.canC.KOMBI_412h
import com.rndash.mbheadunit.nativeCan.canC.MS_608h
import java.util.*
import kotlin.math.min

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class MPGDisplay : UIFragment(1000) {
    lateinit var mpg_text: TextView
    lateinit var avg_mpg_text: TextView
    lateinit var tank_mpg: TextView
    lateinit var fuel_usage: TextView
    lateinit var fuel_consumed_curr: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mpg_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mpg_text = view.findViewById(R.id.text_mpg)
        avg_mpg_text = view.findViewById(R.id.text_avg_mpg)
        tank_mpg = view.findViewById(R.id.text_reset_mpg)
        fuel_usage = view.findViewById(R.id.text_consumed_journey)
        fuel_consumed_curr = view.findViewById(R.id.text_consumed_curr)

        timerTask = {
            activity?.runOnUiThread {
                fuel_consumed_curr.text = String.format("Fuel usage: %4d ul/s", CarData.fuelCurrent.toInt())
                fuel_usage.text = String.format("Tank Level: %2.1f%% (R: %3d%% - L: %3d%%) - %2.1f L",
                        SAM_H_A2.get_tank_fs_b().toFloat() / 2,
                        SAM_H_A2.get_tank_ge_re(),
                        SAM_H_A2.get_tank_ge_li(),
                        62.0 * ((SAM_H_A2.get_tank_fs_b().toFloat() / 2) / 100.0)
                )

                when {
                    CarData.currSpd == 0 -> {
                        // Idle so 0 MPG!
                        mpg_text.setTextColor(Color.WHITE)
                        mpg_text.text = "Current: 0.0 MPG (Idle)"
                    }
                    CarData.fuelCurrent == 0.0 -> {
                        // 0 Fuel used, (Infinite MPG!)
                        mpg_text.setTextColor(Color.GREEN)
                        mpg_text.text = "Current: Inf MPG (REGEN ACTIVE!)"

                    }
                    // Using fuel and cruising
                    else -> {
                        // calculate how much fuel used in 1km based on current consumption
                        val km_per_l = CarData.currSpd / (3600.0 * (CarData.fuelCurrent / 1000000.0))
                        // TODO add Europe units (KM/L)
                        // Convert to MPG
                        // UK MPG is km_l * 2.824809363
                        val mpg = km_per_l * 2.824809363 // TODO Add US MPG (km_l * 2.35215)
                        when {
                            // Set colour of text based on MPG
                            mpg >= 40 -> mpg_text.setTextColor(Color.WHITE)
                            mpg >= 20 -> mpg_text.setTextColor(Color.parseColor("#FF8C00"))
                            else -> mpg_text.setTextColor(Color.RED)
                        }
                        // Display current MPG
                        mpg_text.text = String.format("Current: %3.1f MPG", min(999.9, mpg))
                    }
                }
                // How far have we travelled since engine on?
                tank_mpg.text = String.format("Trip distance: %2.2f miles", CarData.tripDistance)
                // If trip has gone a distance and used some fuel, calculate average MPG
                // based on running totals of distance and fuel usage
                if (CarData.tripDistance != 0.0 && CarData.tripFuelUsed != 0L) {
                    avg_mpg_text.text = String.format("Average MPG: %2.1f",
                            (CarData.tripDistance / (CarData.tripFuelUsed / 1000000.0)) * 2.824809363)
                }
            }
        }
    }
}