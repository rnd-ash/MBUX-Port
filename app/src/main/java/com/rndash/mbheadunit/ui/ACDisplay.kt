package com.rndash.mbheadunit.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.nativeCan.canB.*
import java.nio.ByteBuffer
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ACDisplay : UIFragment() {

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
                    fan_speed_text.text = String.format("Fan speed: %3d %%", KLA_A1.get_geb_lstg())
                    interior_text.text = String.format("Interior temp: %2.1f C", (KLA_A1.get_t_innen_kla().toFloat() / 2.0))
                    exterior_text.text = String.format("Exterior temp: %2.1f C", ((SAM_V_A2.get_t_aussen_b().toFloat() / 2.0) - 40))
                    if (!THL_A1.get_fhl_auf()) {
                        rl_window_text.text = "0%"
                    } else {
                        rl_window_text.text = String.format("%3d %%", THL_A1.get_feste_hl())
                    }
                    if (!THR_A1.get_fhr_auf()) {
                        rl_window_text.text = "0%"
                    } else {
                        rr_window_text.text = String.format("%3d %%", THR_A1.get_feste_hr())
                    }
                    if (!TVL_A3.get_fvl_auf()) {
                        fl_window_text.text = "0%"
                    } else {
                        fl_window_text.text = String.format("%3d %%", TVL_A3.get_feste_vl())
                    }
                    if (!TVR_A3.get_fvr_auf()) {
                        fr_window_text.text = "0%"
                    } else {
                        fr_window_text.text = String.format("%3d %%", TVR_A3.get_feste_vr())
                    }
                    up_flap_pos.text = "Windshield: ${KLA_A1.get_lko_vorn()}"
                    cent_flap_pos.text = "Center: ${KLA_A1.get_lkm_vorn()}"
                    low_flap_pos.text = "Footwell: ${KLA_A1.get_lku_vorn()}"
                }
            }
        }, 0, 250)
    }

    private fun getWindowPercent() {

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