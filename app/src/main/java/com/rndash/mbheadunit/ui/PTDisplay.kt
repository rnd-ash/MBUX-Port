package com.rndash.mbheadunit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.CarData
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.nativeCan.canC.*
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class PTDisplay : UIFragment(250) {
    var isInPage = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.engine_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isInPage = true
        super.onViewCreated(view, savedInstanceState)
        val engCoolant = view.findViewById<TextView>(R.id.engine_coolant_temp)
        val engOil = view.findViewById<TextView>(R.id.engine_oil_temp)
        val engIntake = view.findViewById<TextView>(R.id.engine_intake_temp)
        val engFuel = view.findViewById<TextView>(R.id.engine_fuel_consump)
        val engRpm = view.findViewById<TextView>(R.id.engine_rpm)
        val transTemp = view.findViewById<TextView>(R.id.trans_temp)
        val transTorque = view.findViewById<TextView>(R.id.trans_torque)
        val transTC = view.findViewById<TextView>(R.id.trans_tc_state)
        val turbineRPM = view.findViewById<TextView>(R.id.turbine_rpm)

        timerTask = {
            val tcrpm = GS_338h.get_nturbine()
            val engrpm = MS_308h.get_nmot()
            val mx = max(tcrpm, engrpm)
            val mn = min(tcrpm, engrpm)
            val slip = if (tcrpm == 0 || engrpm == 0) 0 else mx - mn
            val slipPerc = if (tcrpm == 0 || engrpm == 0) 0.0f else (mn.toFloat() / mx.toFloat()) * 100.0f

            activity?.runOnUiThread {
                engCoolant.text = String.format("Coolant temperature: %2d C", MS_608h.get_t_mot() - 40)
                engOil.text = String.format("Oil temperature: %2d C", MS_308h.get_t_oel() - 40)
                engIntake.text = String.format("Intake temperature: %2d C", MS_608h.get_t_luft() - 40)
                engFuel.text = String.format("Fuel usage: %4d ul/s", CarData.fuelCurrent.toInt())
                engRpm.text = String.format("Engine speed: %4d RPM", MS_308h.get_nmot())

                transTemp.text = String.format("Oil temperature: %2d C", GS_418h.get_t_get() - 40)
                transTorque.text = String.format("Torque available: %3d Nm", MS_312h.get_m_max_atl() / 2)
                transTC.text = String.format("TC Slip: %4d RPM (%2.1f %%)", slip, slipPerc)
                turbineRPM.text = String.format("Turbine speed: %4d RPM", GS_338h.get_nturbine())
            }
        }
    }
}