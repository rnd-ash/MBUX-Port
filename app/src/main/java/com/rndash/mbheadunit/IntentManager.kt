package com.rndash.mbheadunit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class IntentManager() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {i ->
            Log.d("INTENT_MGR","INCOMMING INTENT - ACTION ${i.action}")
            i.extras?.let { e ->
                e.keySet().forEach {
                    Log.d("INTENT_MGR","-->E: $it - ${e.get(it)}")
                }
            }
            if (intent.action == "com.microntek.bt.report") {
                i.extras?.getString("music_info")?.let {
                    it.split("\n").let { data ->
                        BTMusic.setName(data[0])
                        BTMusic.setArtist(data[1])
                        BTMusic.setAlbum(data[2])
                    }
                }
            }
        }
    }
}