package com.rndash.mbheadunit.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.canData.CanBusB
import com.rndash.mbheadunit.warnings.DeadBulb
import com.rndash.mbheadunit.warnings.MBUXDialog
import com.rndash.mbheadunit.warnings.Seatbelts
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ACDisplay : Fragment() {

    lateinit var fan_speed_text : TextView
    lateinit var interior_text: TextView
    lateinit var exterior_text: TextView
    lateinit var fr_window_text: TextView
    lateinit var fl_window_text: TextView
    lateinit var rr_window_text: TextView
    lateinit var rl_window_text: TextView

    lateinit var up_flap_pos: TextView
    lateinit var cent_flap_pos: TextView
    lateinit var low_flap_pos: TextView

    var isInPage = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ac_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isInPage = true
        super.onViewCreated(view, savedInstanceState)
        fan_speed_text = view.findViewById(R.id.text_fan_speed)
        interior_text = view.findViewById(R.id.text_interior_temp)
        exterior_text = view.findViewById(R.id.text_exterior_temp)

        fr_window_text = view.findViewById(R.id.window_fr)
        fl_window_text = view.findViewById(R.id.window_fl)
        rr_window_text = view.findViewById(R.id.window_rr)
        rl_window_text = view.findViewById(R.id.window_rl)

        up_flap_pos = view.findViewById(R.id.upper_vent)
        cent_flap_pos = view.findViewById(R.id.mid_vent)
        low_flap_pos = view.findViewById(R.id.low_vent)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if(!isInPage){return}
                activity?.runOnUiThread {
                    fan_speed_text.text = String.format("Fan speed: %3d %%", CanBusB.kla_a1.getFanSpeedPercent())
                    interior_text.text = String.format("Interior temp: %2.1f C", CanBusB.kla_a1.getInteriorTemp())
                    exterior_text.text = String.format("Exterior temp: %2.1f C", CanBusB.sam_v_a2.getOutsideTemp())
                    rl_window_text.text = String.format("%3d %%", CanBusB.thlA1.getWindowPositionPercent())
                    rr_window_text.text = String.format("%3d %%", CanBusB.thrA1.getWindowPositionPercent())
                    fl_window_text.text = String.format("%3d %%", CanBusB.tvlA3.getWindowPositionPercent())
                    fr_window_text.text = String.format("%3d %%", CanBusB.tvrA3.getWindowPositionPercent())
                    up_flap_pos.text = "Windshield: ${CanBusB.kla_a1.getUpFlapPosition().str}"
                    cent_flap_pos.text = "Center: ${CanBusB.kla_a1.getCenterFlapPosition().str}"
                    low_flap_pos.text = "Footwell: ${CanBusB.kla_a1.getLowerFlapPosition().str}"
                }
            }
        }, 0, 100)
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