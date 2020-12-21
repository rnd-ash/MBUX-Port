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
    private fun millis() = System.currentTimeMillis()

    @Volatile
    private var leftIndicatorOn = false
    @Volatile
    private var rightIndicatorOn = false
    @Volatile
    private var leftInidicatorCutoff = millis()
    @Volatile
    private var rightIndicatorCutOff = millis()
    @Volatile
    private var blinkerStateChange = false

    @Volatile
    private var fogOn = false
    @Volatile
    private var dippedOn = false
    @Volatile
    private var fogCutoff = millis()
    @Volatile
    private var dippedCutOff = millis()
    @Volatile
    private var lightStateChange = false

    //private var sha1 = CanFrame(0x000A, 'B', byteArrayOf(0x00,0x00,0x00,0x00,0x00,0x00))
    private var sha3 = CanFrame(0x000E, 'B', byteArrayOf(0x00, 0x00))
    private var sha5 = CanFrame(0x0230, 'B', byteArrayOf(0x00, 0x00))

    fun getSam3() : CanFrame = sha3
    fun getSam5() : CanFrame = sha5

    private var lastSampleTime = millis()
    private var partyThread : Thread? = null
    private fun threadLoop() {
        println("Party mode thread start!")
        var millis: Long
        while(true) {
            if (!isEngineOn()) {
                millis = System.currentTimeMillis()
                // -- DO BLINKER STUFF --
                if (millis >= leftInidicatorCutoff && leftIndicatorOn) {
                    leftIndicatorOn = false
                    blinkerStateChange = true
                }
                // Do i turn off the right indicator?
                if (millis >= rightIndicatorCutOff && rightIndicatorOn) {
                    rightIndicatorOn = false
                    blinkerStateChange = true
                }

                if (blinkerStateChange) {
                    blinkerStateChange = false
                    sha3.clear()
                    SAM_H_A3.set_bli_li_ein(sha3, leftIndicatorOn)
                    SAM_H_A3.set_bli_re_ein(sha3, rightIndicatorOn)
                    if (leftIndicatorOn || rightIndicatorOn) {
                        SAM_H_A3.set_hell_blink(
                            sha3,
                            0xFF
                        ) // FF as we will tell SAM When to turn off
                    } else {
                        SAM_H_A3.set_hell_blink(sha3, 0x00) // Tell SAM to turn off
                    }
                    CarComm.sendFrame(sha3)
                }

                // Do lights stuff

                if (millis >= fogCutoff && fogOn) {
                    fogOn = false
                    lightStateChange = true
                }
                // Do i turn off the right indicator?
                if (millis >= dippedCutOff && dippedOn) {
                    dippedOn = false
                    lightStateChange = true
                }

                if (lightStateChange) {
                    lightStateChange = false
                    sha5.clear()
                    SAM_H_A5.set_nsw_ein_edw(sha5, fogOn)
                    SAM_H_A5.set_abl_ein_edw(sha5, dippedOn)
                    if (fogOn || dippedOn) {
                        SAM_H_A5.set_hell_edw(sha5, 0xFF) // FF as we will tell SAM When to turn off
                    } else {
                        SAM_H_A5.set_hell_edw(sha5, 0x00) // Tell SAM to turn off
                    }
                    CarComm.sendFrame(sha5)
                }
            }
            // -- Finally sleep a bit
            try {
                Thread.sleep(5)
            } catch (e: InterruptedException) {
                println("Party thread terminated by host!")
                break
            }
        }
    }

    // Check if engine is on. If it is party thread MUST exit
    fun isEngineOn() : Boolean {
        //return EZS_A2.get_n_mot() != 65535 && EZS_A2.get_n_mot() != 0
        return false // Engine is always off for 'testing'
    }

    fun activateLeftBlinker(durationMs: Int) {
        leftInidicatorCutoff = millis() + durationMs
        leftIndicatorOn = durationMs > 0
        blinkerStateChange = true
    }

    fun activateRightBlinker(durationMs: Int) {
        rightIndicatorCutOff = millis() + durationMs
        rightIndicatorOn = durationMs > 0
        blinkerStateChange = true
    }

    fun activateFog(durationMs: Int) {
        fogCutoff = millis() + durationMs
        fogOn = durationMs > 0
        lightStateChange = true
    }

    fun activateDipped(durationMs: Int) {
        dippedCutOff = millis() + durationMs
        dippedOn = durationMs > 0
        lightStateChange = true
    }

    fun activateHazards(durationMs: Int) {
        activateLeftBlinker(durationMs)
        activateRightBlinker(durationMs)
    }

    fun isRightIndicatorOn(): Boolean = rightIndicatorOn
    fun isLeftIndicatorOn(): Boolean = leftIndicatorOn
    fun isHazardOn(): Boolean = rightIndicatorOn && leftIndicatorOn
    fun isDippedOn(): Boolean = dippedOn
    fun isFogOn(): Boolean = fogOn


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