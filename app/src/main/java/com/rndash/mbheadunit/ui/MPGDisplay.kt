package com.rndash.mbheadunit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.nativeCan.canB.KOMBI_A1
import com.rndash.mbheadunit.nativeCan.canB.SAM_H_A2
import com.rndash.mbheadunit.nativeCan.canB.SAM_V_A2
import com.rndash.mbheadunit.nativeCan.canC.MS_608h
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class MPGDisplay : Fragment() {
    lateinit var mpg_text: TextView
    lateinit var avg_mpg_text: TextView
    lateinit var tank_mpg: TextView
    lateinit var fuel_usage: TextView
    lateinit var fuel_consumed_curr: TextView
    var isInPage = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mpg_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isInPage = true
        super.onViewCreated(view, savedInstanceState)
        mpg_text = view.findViewById(R.id.text_mpg)
        avg_mpg_text = view.findViewById(R.id.text_avg_mpg)
        tank_mpg = view.findViewById(R.id.text_reset_mpg)
        fuel_usage = view.findViewById(R.id.text_consumed_journey)
        fuel_consumed_curr = view.findViewById(R.id.text_consumed_curr)

        Timer().schedule(object: TimerTask() {
            override fun run() {
                if (!isInPage){return}
                //val consumedLitres = CanBusC.getFuelConsumedTotal() / 1000000.0 //ul  to L
                activity?.runOnUiThread {
                    fuel_consumed_curr.text = String.format("Fuel usage: %4d ul/s", MS_608h.get_vb())
                    fuel_usage.text = String.format("Tank Level: %2.1f%% (R: %3d%% - L: %3d%%)",
                            SAM_H_A2.get_tank_fs_b().toFloat() / 2,
                            SAM_H_A2.get_tank_ge_re(),
                            SAM_H_A2.get_tank_ge_li()
                    )
                        //mpg_text.text = String.format("Current: %2.1f MPG", CanBusC.getMPG()
                }
            }
        }, 0, 500)
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