package com.rndash.mbheadunit.canData

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.FullscreenActivity
import com.rndash.mbheadunit.canData.canB.KLA_A1
import com.rndash.mbheadunit.canData.canB.KOMBI_A5
import com.rndash.mbheadunit.canData.canB.SAM_V_A2
import kotlin.properties.Delegates


@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object CanBusB  {
    var kla_a1 = KLA_A1()
    var sam_v_a2 = SAM_V_A2()
    var kombiA5 = KOMBI_A5()
    // On init we don't know what page we are on!
    val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    internal var curr_keys : ArrayList<WheelKey> by Delegates.observable(arrayListOf()) { _, o, n ->
        if (n != o) {
            println("New key map: $n")
            //toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,200)
            if (WheelKey.VOL_UP in n) {
                FullscreenActivity.modifyVolume(true)
            } else if (WheelKey.VOL_DN in n) {
                FullscreenActivity.modifyVolume(false)
            }
        }
    }
    internal var ic_page : KombiPage by Delegates.observable(KombiPage.OTHER) { _, o, n ->
        if (n != o) {
            println("New page: $n")
        }
    }

    enum class WheelKey {
        VOL_UP,
        VOL_DN,
        ARR_UP,
        ARR_DN,
        TEL_UP,
        TEL_DN
    }

    enum class KombiPage() {
        AUDIO,
        TELEPHONE,
        OTHER
    }

    fun updateFrames(incoming: CarCanFrame) {
        when(incoming.canID) {
            kla_a1.id -> kla_a1.parseFrame(incoming)
            sam_v_a2.id -> sam_v_a2.parseFrame(incoming)
            kombiA5.id -> kombiA5.parseFrame(incoming)
        }
    }
}