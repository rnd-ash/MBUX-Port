package com.rndash.mbheadunit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rndash.mbheadunit.BTMusic
import com.rndash.mbheadunit.R
import com.rndash.mbheadunit.nativeCan.canB.EZS_A11
import com.rndash.mbheadunit.nativeCan.canC.*
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
        Timer().schedule(object: TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (BTMusic.isPlaying()) {
                        trackName.text = "Playing ${BTMusic.getTrackName()} by ${BTMusic.getTrackArtist()}"
                    } else {
                        trackName.text = "Not Playing BT"
                    }
                }
            }
        }, 0, 250)
    }
}