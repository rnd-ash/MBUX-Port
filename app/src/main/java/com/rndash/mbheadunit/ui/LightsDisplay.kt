package com.rndash.mbheadunit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.BTMusic
import com.rndash.mbheadunit.CanFrame
import com.rndash.mbheadunit.CarComm
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.car.PartyMode
import com.rndash.mbheadunit.nativeCan.canB.EZS_A2
import com.rndash.mbheadunit.nativeCan.canB.SAM_H_A3
import com.rndash.mbheadunit.nativeCan.canC.*
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class LightsDisplay : Fragment() {
    var isInPage = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.lights_display, container, false)
    }

    private var indicator_control = false
    private var fog_control = false
    lateinit var indicator_toggle : Switch
    lateinit var indicator_seek : SeekBar
    lateinit var indicator_text : TextView

    lateinit var fog_toggle : Switch
    lateinit var fog_seek : SeekBar
    lateinit var fog_text : TextView

    var nextCheckL = System.currentTimeMillis()
    var nextCheckH = System.currentTimeMillis()
    val controlThread = Thread() {
        var lMode = false
        var hMode = 0
        while(true) {
            if (fog_toggle.isChecked) {
                if (System.currentTimeMillis() >= nextCheckL + fog_seek.progress * 2L) {
                    nextCheckL = System.currentTimeMillis()
                    when (lMode) {
                        true -> PartyMode.activateDipped(fog_seek.progress.toLong())
                        false -> PartyMode.activateFog(fog_seek.progress.toLong())
                    }
                    lMode = !lMode
                }
            }
            if (indicator_toggle.isChecked) {
                if (System.currentTimeMillis() >= nextCheckH + indicator_seek.progress * 2L) {
                    nextCheckH = System.currentTimeMillis()
                    when (hMode) {
                        0 -> PartyMode.activateHazards(indicator_seek.progress.toLong())
                        1 -> PartyMode.activateLeftBlinker(indicator_seek.progress.toLong())
                        else -> PartyMode.activateRightBlinker(indicator_seek.progress.toLong())
                    }
                    hMode++
                    if (hMode == 3) {
                        hMode = 0
                    }
                }
            }
            println("AMP: ${BTMusic.getAmplitude()}")
            Thread.sleep(100)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isInPage = true
        super.onViewCreated(view, savedInstanceState)

        indicator_toggle = view.findViewById(R.id.indicator_toggle)
        indicator_seek = view.findViewById(R.id.hazard_seek)
        indicator_text = view.findViewById(R.id.indicator_text)

        fog_toggle = view.findViewById(R.id.fog_toggle)
        fog_seek = view.findViewById(R.id.fog_seek)
        fog_text = view.findViewById(R.id.fog_text)

        indicator_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (PartyMode.isEngineOn()) {
                Toast.makeText(activity, "Error. Engine On", Toast.LENGTH_SHORT).show()
            } else {
                indicator_control = isChecked
            }
        }
        fog_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (PartyMode.isEngineOn()) {
                Toast.makeText(activity, "Error. Engine On", Toast.LENGTH_SHORT).show()
            } else {
                fog_control = isChecked
            }
        }

        indicator_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                indicator_text.text = "$progress ms"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        fog_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fog_text.text = "$progress ms"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        Timer().schedule(object: TimerTask() {
            override fun run() {
            }
        }, 0, 250)
        PartyMode.startThread()
        controlThread.start()
    }


    override fun onPause() {
        super.onPause()
        isInPage = false
        PartyMode.stopThread()
        BTMusic.tearDownSampler()
    }

    override fun onResume() {
        super.onResume()
        isInPage = true
        PartyMode.startThread()
        BTMusic.setupSampler()
    }
}