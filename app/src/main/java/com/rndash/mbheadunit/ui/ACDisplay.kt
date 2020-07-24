package com.rndash.mbheadunit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.canData.CanBusB
import org.w3c.dom.Text
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ACDisplay : Fragment() {

    lateinit var fan_speed_text : TextView
    lateinit var interior_text: TextView
    lateinit var exterior_text: TextView
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

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if(!isInPage){return}
                activity?.runOnUiThread {
                    fan_speed_text.text = String.format("Fan speed: %3d %%", CanBusB.kla_a1.getFanSpeedPercent())
                    interior_text.text = String.format("Interior temp: %2.1f C", CanBusB.kla_a1.getInteriorTemp())
                    exterior_text.text = String.format("Exterior temp: %2.1f C", CanBusB.sam_v_a2.getOutsideTemp())
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