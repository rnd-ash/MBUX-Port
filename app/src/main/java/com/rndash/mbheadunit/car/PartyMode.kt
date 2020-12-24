package com.rndash.mbheadunit.car

import com.rndash.mbheadunit.BTMusic
import com.rndash.mbheadunit.CanFrame
import com.rndash.mbheadunit.CarComm
import com.rndash.mbheadunit.nativeCan.canB.EZS_A2
import com.rndash.mbheadunit.nativeCan.canB.SAM_H_A3
import com.rndash.mbheadunit.nativeCan.canB.SAM_H_A5
import com.rndash.mbheadunit.nativeCan.canB.SAM_V_A1
import java.lang.Long.max

/**
 * Object that deals with special effects such as disco lights on the car
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
object PartyMode {
    @Suppress("NOTHING_TO_INLINE")
    private inline fun millis() = System.currentTimeMillis()

    @Volatile
    private var leftInidicatorCutoff = millis()
    @Volatile
    private var rightIndicatorCutOff = millis()

    @Volatile
    private var fogCutoff = millis()
    @Volatile
    private var dippedCutOff = millis()

    private var sam3CutOff = false
    private var sam5CutOff = false

    //private var sha1 = CanFrame(0x000A, 'B', byteArrayOf(0x00,0x00,0x00,0x00,0x00,0x00))
    private var sha3 = CanFrame(0x000E, 'B', byteArrayOf(0x00, 0x00))
    private var sha5 = CanFrame(0x0230, 'B', byteArrayOf(0x00, 0x00))

    fun getSam3() : CanFrame = sha3
    fun getSam5() : CanFrame = sha5

    private var partyThread : Thread? = null
    private fun threadLoop() {
        println("Party mode thread start!")
        while(true) {
            if (!isEngineOn()) {
                // -- DO BLINKER STUFF --
                val isLeftOn = isLeftIndicatorOn()
                val isRightOn = isRightIndicatorOn()

                SAM_H_A3.set_bli_li_ein(sha3, isLeftOn)
                SAM_H_A3.set_bli_re_ein(sha3, isRightOn)
                if (isLeftOn || isRightOn) {
                    sam3CutOff = false
                    SAM_H_A3.set_hell_blink(sha3, 0xFF) // FF as we will tell SAM When to turn off
                    CarComm.sendFrame(sha3)
                } else if (!isLeftOn && !isRightOn && !sam3CutOff) {
                    sam3CutOff = true
                    sha3.clear()
                    CarComm.sendFrame(sha3)
                }

                // Do dipped / fog stuff
                val fogOn = isFogOn()
                val dippedOn = isDippedOn()

                SAM_H_A5.set_nsw_ein_edw(sha5, fogOn)
                SAM_H_A5.set_abl_ein_edw(sha5, dippedOn)
                if (fogOn || dippedOn) {
                    sam5CutOff = false
                    SAM_H_A5.set_hell_edw(sha5, 0xFF) // FF as we will tell SAM When to turn off
                    CarComm.sendFrame(sha5)
                } else if (!fogOn && !dippedOn && !sam5CutOff){
                    sam5CutOff = true
                    sha5.clear()
                    CarComm.sendFrame(sha5)
                }
            }
            // -- Finally sleep a bit
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                println("Party thread terminated by host!")
                break
            }
        }
    }

    // Check if engine is on. If it is party thread MUST exit
    fun isEngineOn() : Boolean {
        return EZS_A2.get_n_mot() != 65535 && EZS_A2.get_n_mot() != 0
        //return false // Engine is always off for 'testing'
    }

    fun activateLeftBlinker(durationMs: Int) {
        leftInidicatorCutoff = millis() + durationMs
    }

    fun activateRightBlinker(durationMs: Int) {
        rightIndicatorCutOff = millis() + durationMs
    }

    fun activateFog(durationMs: Int) {
        fogCutoff = millis() + durationMs
    }

    fun activateDipped(durationMs: Int) {
        dippedCutOff = millis() + durationMs
    }

    fun activateHazards(durationMs: Int) {
        activateLeftBlinker(durationMs)
        activateRightBlinker(durationMs)
    }

    fun isRightIndicatorOn(): Boolean = rightIndicatorCutOff > millis()
    fun isLeftIndicatorOn(): Boolean = leftInidicatorCutoff > millis()
    fun isHazardOn(): Boolean = isRightIndicatorOn() && isLeftIndicatorOn()
    fun isDippedOn(): Boolean = dippedCutOff > millis()
    fun isFogOn(): Boolean = fogCutoff > millis()


    fun stopThread() {
        partyThread?.let {
            if (it.isAlive) {
                it.interrupt()
            }
        }
        partyThread = null
    }

    fun startThread() {
        if (partyThread == null) {
            partyThread = Thread { threadLoop() }
            partyThread?.start()
        }
    }
}