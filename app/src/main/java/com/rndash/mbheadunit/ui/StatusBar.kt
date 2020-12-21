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
import com.rndash.mbheadunit.nativeCan.canC.FPC
import com.rndash.mbheadunit.nativeCan.canC.GS_218h
import com.rndash.mbheadunit.nativeCan.canC.GS_418h
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

        val spd_display = view.findViewById<TextView>(R.id.spd_view)
        val gear_display = view.findViewById<TextView>(R.id.gear_disp)
        timerTask = {
            activity?.runOnUiThread {
                if (BTMusic.getTrackName() != "UNKNOWN") {
                    trackName.text =
                        "MUSIC: ${BTMusic.getTrackName()} by ${BTMusic.getTrackArtist()}"
                } else {
                    trackName.text = "Not Playing BT"
                }
            }

            // These 2 calls reset the metrics so not to get an insane number
            val rx = String.format("%.1f", CarComm.getRxRate().toDouble() * 4 / 1000.0)
            val tx = String.format("%.1f", CarComm.getTxRate().toDouble() * 4 / 1000.0)
            // TODO Find why GIC and GZC are swapped
            val targGear = GS_218h.get_gic().toString()
            val currGear = GS_218h.get_gzc().toString()
            val drv_profile = GS_418h.get_fpc()
            val prof = when (GS_418h.get_fpc()) {
                FPC.SNV -> "Signal NA"
                FPC.S -> "Sport"
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
            activity?.runOnUiThread {
                rx_metric.text = "Rx: $rx kb/s"
                tx_metric.text = "Tx: $tx kb/s"
                gear_display.text = gearText
                if (CarData.isSportFeel) {
                    gear_display.setTextColor(Color.RED)
                } else {
                    gear_display.setTextColor(Color.WHITE)
                }
                spd_display.text = "${CarData.currSpd} mph"

                if (CarData.currSpd > 70) {
                    spd_display.setTextColor(Color.RED)
                } else {
                    spd_display.setTextColor(Color.WHITE)
                }
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