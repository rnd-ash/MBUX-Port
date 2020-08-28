package com.rndash.mbheadunit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class IntentManager() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {i ->
            if (intent.action == "com.microntek.bt.report") {
                i.extras?.getString("music_info")?.let {
                    it.split("\n").let { data ->
                        BTMusic.setName(data[0])
                        BTMusic.setArtist(data[1])
                        BTMusic.setAlbum(data[2])
                    }
                }
                i.extras?.getInt("bt_state")?.let {
                    when (it) {
                        87 -> BTMusic.setPlayState(false)
                        88, 0 -> BTMusic.setPlayState(true)
                        else -> println("Unknown play state: $it")
                    }
                }
            } else {
                println("UNKNOWN INTENT - ACTION ${i.action}")
                i.extras?.let { e ->
                    e.keySet().forEach {
                        println("E: $it - ${e.get(it)}")
                    }
                }
            }
        }
    }
}