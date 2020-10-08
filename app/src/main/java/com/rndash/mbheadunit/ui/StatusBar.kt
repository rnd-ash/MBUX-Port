package com.rndash.mbheadunit.ui

import android.content.Intent
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
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class StatusBar : Fragment() {


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
        Timer().schedule(object: TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (BTMusic.getTrackName() != "UNKNOWN") {
                        trackName.text = "MUSIC: ${BTMusic.getTrackName()} by ${BTMusic.getTrackArtist()}"
                    } else {
                        trackName.text = "Not Playing BT"
                    }
                }
            }
        }, 0, 250)

        // These 2 calls reset the metrics so not to get an insane number
        CarComm.getRxRate()
        CarComm.getTxRate()
        Timer().schedule(object: TimerTask() {
            override fun run() {
                val rx = String.format("%.1f", CarComm.getRxRate().toDouble() / 1024.0)
                val tx = String.format("%.1f", CarComm.getTxRate().toDouble() / 1024.0)
                activity?.runOnUiThread {
                    rx_metric.text = "Rx: $rx KB/s"
                    tx_metric.text = "Tx: $tx KB/s"
                }
            }
        }, 0, 1000) // Poll for Rx/Tx every secondk

        trackName.setOnLongClickListener {
            startActivity(Intent(activity, DoomActivity::class.java))
            return@setOnLongClickListener true
        }

        trackNext.setOnClickListener { BTMusic.playNext() }
        trackPrev.setOnClickListener { BTMusic.playPrev() }

    }
}