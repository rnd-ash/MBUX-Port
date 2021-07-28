package com.rndash.mbheadunit.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.*
import com.rndash.mbheadunit.car.PartyMode.isEngineOn
import com.rndash.mbheadunit.nativeCan.canC.*
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class StatusBar : UIFragment(250) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.status_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val trackName = view.findViewById<TextView>(R.id.trackName)
        val trackNext = view.findViewById<ImageView>(R.id.track_next)
        val trackPrev = view.findViewById<ImageView>(R.id.track_prev)
        val rx_metric = view.findViewById<TextView>(R.id.bytes_rx)
        val tx_metric = view.findViewById<TextView>(R.id.bytes_tx)

        val batt_img = view.findViewById<ImageView>(R.id.bat_img)
        val batt_text = view.findViewById<TextView>(R.id.batt_text)

        val esp_img = view.findViewById<ImageView>(R.id.esp_img)

        val spd_display = view.findViewById<TextView>(R.id.spd_view)
        val gear_display = view.findViewById<TextView>(R.id.gear_disp)

        val art_img = view.findViewById<ImageView>(R.id.cc_img)
        timerTask = {
            activity?.runOnUiThread {
                if (BTMusic.getTrackName() != "UNKNOWN") {
                    trackName.text =
                        "${BTMusic.getTrackName()}"
                } else {
                    trackName.text = "No music"
                }
            }

            // These 2 calls reset the metrics so not to get an insane number
            var rx = String.format("%.1f", CarComm.getRxRate().toDouble() * 4 / 1000.0)
            val tx = String.format("%.1f", CarComm.getTxRate().toDouble() * 4 / 1000.0)
            val currGear = GS_218h.get_gic().toString()
            val targGear = GS_218h.get_gzc().toString()
            val drv_profile = GS_418h.get_fpc()
            val prof = when (GS_418h.get_fpc()) {
                FPC.SNV -> "Signal NA"
                FPC.S -> "Standard"
                FPC.C -> "Comfort"
                FPC.M -> "Manual"
                FPC.A -> "Agility"
                else -> "${GS_418h.get_fpc()}"
            }
            // The following 3 profiles count as 'sporty':
            // 1. A (Agility - Although has to be coded to be enabled on 722.x series)
            // 2. M (Manual)
            CarData.isSportFeel = drv_profile == FPC.M || drv_profile == FPC.A

            val gearText: String
            gearText = if (currGear != targGear) {
                "$currGear -> $targGear ($prof)"
            } else {
                "$currGear ($prof)"
            }

            val cc_spd = ART_258h.get_v_art()
            val art_enabled =  false

            activity?.runOnUiThread {
                rx_metric.text = "Rx: $rx kb/s"
                tx_metric.text = "Tx: $tx kb/s"
                gear_display.text = gearText

                batt_text.text = String.format("%.1f V", CarData.batt_voltage)
                batt_img.setImageResource(
                    when {
                        CarData.batt_voltage < 11.5 -> R.drawable.bat_red
                        CarData.batt_voltage < 12.5 -> R.drawable.bat_white
                        else ->R.drawable.bat_green
                    }
                )

                if (CarData.isSportFeel) {
                    gear_display.setTextColor(Color.RED)
                } else {
                    gear_display.setTextColor(Color.WHITE)
                }
                var spd_txt = String.format("%d ", CarData.get_speed().toInt())
                if (cc_spd != 0) {
                    spd_txt += String.format("| %d ", cc_spd)
                }
                spd_txt += if (CarData.isMetric) { "kmh" } else { "mph" }
                if(art_enabled) {
                    art_img.setImageResource(R.drawable.lim_on)
                } else {
                    art_img.setImageResource(R.drawable.lim_off)
                }

                if (CarData.show_esp_warn) {
                    esp_img.visibility = View.VISIBLE
                } else {
                    esp_img.visibility = View.INVISIBLE
                }

                spd_display.text = spd_txt
            }
        }

        trackName.setOnLongClickListener {
            startActivity(Intent(activity, DoomActivity::class.java))
            return@setOnLongClickListener true
        }

        trackNext.setOnClickListener { BTMusic.playNext() }
        trackPrev.setOnClickListener { BTMusic.playPrev() }

    }
}