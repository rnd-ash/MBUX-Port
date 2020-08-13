package com.rndash.mbheadunit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.R
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class StatusBar : Fragment() {

    lateinit var gear_display : ImageView
    lateinit var cruise_img: ImageView
    lateinit var cruise_text: TextView
    lateinit var bat_text: TextView
    lateinit var bat_img: ImageView

    /*
    fun getCruiseData() : Pair<Int, String> {
        val state = CanBusC.getCruiseState()
        return when(state.first) {
            CanBusC.CruiseState.OFF -> Pair(R.drawable.cruise_off, "__")
            CanBusC.CruiseState.ON -> Pair(R.drawable.cruise_on, "${state.second}")
            CanBusC.CruiseState.ARMED -> Pair(R.drawable.cruise_armed, "${state.second}")
        }
    }

    fun getLimData() : Pair<Int, String> {
        val state = CanBusC.getLimState()
        return when(state.first) {
            CanBusC.CruiseState.OFF -> Pair(R.drawable.lim_off, "__")
            CanBusC.CruiseState.ON -> Pair(R.drawable.lim_on, "${state.second}}")
            CanBusC.CruiseState.ARMED -> Pair(R.drawable.lim_armed, "__")
        }
    }
    */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.status_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gear_display = view.findViewById(R.id.gear_display)
        cruise_img = view.findViewById(R.id.cruise_display)
        cruise_text = view.findViewById(R.id.cruise_text)
        bat_img = view.findViewById(R.id.batt_icon)
        bat_text = view.findViewById(R.id.batt_text)

        Timer().schedule(object: TimerTask() {
            override fun run() {
                /*
                val resource = when (CanBusC.getTransmissionProgram()) {
                    CanBusC.DriveProgram.MANUAL -> {
                        val x = when(CanBusC.getGear()) {
                            CanBusC.Gear.P -> R.drawable.gear_p
                            CanBusC.Gear.N -> R.drawable.gear_n
                            CanBusC.Gear.R1 -> R.drawable.gear_r1
                            CanBusC.Gear.R2 -> R.drawable.gear_r2
                            CanBusC.Gear.D1 -> R.drawable.gear_m1
                            CanBusC.Gear.D2 -> R.drawable.gear_m2
                            CanBusC.Gear.D3 -> R.drawable.gear_m3
                            CanBusC.Gear.D4 -> R.drawable.gear_m4
                            CanBusC.Gear.D5 -> R.drawable.gear_m5
                            CanBusC.Gear.D6 -> R.drawable.gear_m6
                            CanBusC.Gear.D7 -> R.drawable.gear_m7
                            else -> R.drawable.gear_n
                        }
                        x
                    }
                    CanBusC.DriveProgram.SPORT, CanBusC.DriveProgram.COMFORT -> {
                        val x : Int = when(CanBusC.getGear()) {
                            CanBusC.Gear.P -> R.drawable.gear_p
                            CanBusC.Gear.N -> R.drawable.gear_n
                            CanBusC.Gear.R1 -> R.drawable.gear_r1
                            CanBusC.Gear.R2 -> R.drawable.gear_r2
                            CanBusC.Gear.D1 -> R.drawable.gear_d1
                            CanBusC.Gear.D2 -> R.drawable.gear_d2
                            CanBusC.Gear.D3 -> R.drawable.gear_d3
                            CanBusC.Gear.D4 -> R.drawable.gear_d4
                            CanBusC.Gear.D5 -> R.drawable.gear_d5
                            CanBusC.Gear.D6 -> R.drawable.gear_d6
                            CanBusC.Gear.D7 -> R.drawable.gear_d7
                            else -> R.drawable.gear_n
                        }
                        x
                    }
                    CanBusC.DriveProgram.UNKNOWN -> R.drawable.gear_unknown
                }

                val bat_voltage = CanBusB.ezsA11.getBattVoltage()
                val bat_image : Int = when {
                    bat_voltage < 12.0 ->  R.drawable.bat_red
                    bat_voltage < 13.0 -> R.drawable.bat_white
                    else -> R.drawable.bat_green
                }

                val cruise_data = getCruiseData()
                activity?.runOnUiThread {
                    gear_display.scaleX = 0.75F
                    gear_display.scaleY = 0.75F
                    gear_display.setImageResource(resource)
                    cruise_img.setImageResource(cruise_data.first)
                    cruise_text.text = cruise_data.second
                    bat_img.setImageResource(bat_image)
                    bat_text.text = String.format("%2.2f V", bat_voltage)
                }
                */
            }
        }, 0, 200)
    }
}