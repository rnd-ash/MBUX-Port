package com.rndash.mbheadunit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.R
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class PTDisplay : Fragment() {
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

        Timer().schedule(object: TimerTask() {
            override fun run() {
                if (!isInPage){return}
                activity?.runOnUiThread {
                    /*
                    engCoolant.text = String.format("Coolant temperature: %2d C", CanBusC.ms608.getMotorTemp())
                    engOil.text = String.format("Oil temperature: %2d C", CanBusC.ms308.getOilTemp())
                    engIntake.text = String.format("Intake temperature: %2d C", CanBusC.ms608.getIntakeTemp())
                    engFuel.text = String.format("Fuel usage: %4d ul/s", CanBusC.ms608.getFuelConsumption())
                    engRpm.text = String.format("Engine speed: %4d RPM", CanBusC.ms308.getEngineRPM())

                    transTemp.text = String.format("Oil temperature: %2d C", CanBusC.gs418.getTransOilTemp())
                    transTorque.text = String.format("Torque: %4d Nm", CanBusC.gs218.getEngineTorque())
                    transTC.text = String.format("TC clutch duty: %3d%%", CanBusC.getTCDuty())
                    turbineRPM.text = String.format("Turbine speed: %4d RPM", CanBusC.gs338.getTurbineSpeed())

                     */
                }
            }
        }, 0, 250)
    }

    override fun onPause() {
        super.onPause()
        isInPage = false
    }

    override fun onResume() {
        super.onResume()
        isInPage = true
    }
}