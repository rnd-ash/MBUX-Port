package com.rndash.mbheadunit.canData

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.CarComm
import com.rndash.mbheadunit.FullscreenActivity
import com.rndash.mbheadunit.canData.canB.*
import com.rndash.mbheadunit.canData.canB.kombiDisplay.ICDisplay
import kotlin.math.ceil
import kotlin.properties.Delegates


@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object CanBusB  {
    var kla_a1 = KLA_A1()
    var sam_v_a2 = SAM_V_A2()
    var kombiA5 = KOMBI_A5()
    var ezsA11 = EZS_A11()
    var tvrA2 = TVR_A2()
    var thrA1 = THR_A1()
    var thlA1 = THL_A1()
    var tvrA3 = TVR_A3()
    var tvlA3 = TVL_A3()
    var kombiA1 = KOMBI_A1()
    
    internal var curr_keys : ArrayList<WheelKey> by Delegates.observable(arrayListOf()) { _, o, n ->
        if (n != o) {
            println("New key map: $n")
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
            kombiA1.id -> kombiA1.parseFrame(incoming)
            sam_v_a2.id -> sam_v_a2.parseFrame(incoming)
            kombiA5.id -> kombiA5.parseFrame(incoming)
            ezsA11.id -> ezsA11.parseFrame(incoming)
            tvrA2.id -> tvrA2.parseFrame(incoming)
            thrA1.id -> thrA1.parseFrame(incoming)
            thlA1.id -> thlA1.parseFrame(incoming)
            tvrA3.id -> tvrA3.parseFrame(incoming)
            tvlA3.id -> tvlA3.parseFrame(incoming)
            0x01D0 -> ICDisplay.processKombiResponse(incoming)
        }
    }

    internal fun getWindowOpenPercent(raw: Int, isOpen: Boolean) : Int {
        return if (!isOpen) {
            0
        } else {
            val parsed = 5 * ceil(raw / 10.0 / 5.0).toInt()
            if (parsed > 100) {
                100
            } else parsed
        }
    }

    object WindowManager {
        fun setWindowPositions(fr: Int, fl: Int, rr: Int, rl: Int) {
            val targetrl = 5 * ceil(rl / 5.0).toInt()
            val targetrr = 5 * ceil(rr / 5.0).toInt()

            var fr_done = false
            var fl_done = false
            var rr_done = false
            var rl_done = false

            val canFrame = tvrA2
            while(!rr_done || !rl_done) {
                when {
                    thlA1.getWindowPositionPercent() > targetrl -> canFrame.setCloseRearLeft()
                    thlA1.getWindowPositionPercent() < targetrl -> canFrame.setOpenRearLeft()
                    else -> {
                        canFrame.setNeutralRearLeft()
                        rl_done = true
                    }
                }
                when {
                    thrA1.getWindowPositionPercent() > targetrr -> canFrame.setCloseRearRight()
                    thrA1.getWindowPositionPercent() < targetrr -> canFrame.setOpenRearRight()
                    else -> {
                        canFrame.setNeutralRearRight()
                        rr_done = true
                    }
                }
                CarComm.sendFrame(CarComm.CANBUS_ID.CANBUS_B, canFrame.createCanFrame())
                Thread.sleep(10) // IMPORTANT DON'T SPAM CANBUS!
            }
            Thread.sleep(50) // Give can a break before we exit
        }
    }
}