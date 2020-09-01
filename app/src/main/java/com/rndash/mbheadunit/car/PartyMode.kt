package com.rndash.mbheadunit.car

import com.rndash.mbheadunit.BTMusic
import com.rndash.mbheadunit.CanFrame
import com.rndash.mbheadunit.CarComm
import com.rndash.mbheadunit.nativeCan.canB.EZS_A2
import com.rndash.mbheadunit.nativeCan.canB.SAM_H_A3
import com.rndash.mbheadunit.nativeCan.canB.SAM_H_A5

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

    private var sha3 = CanFrame(0x000E, 'B', byteArrayOf(0x00, 0x00))
    private var sha5 = CanFrame(0x0230, 'B', byteArrayOf(0x00, 0x00))

    private var lastSampleTime = millis()
    private var partyThread : Thread? = null
    private fun threadLoop() {
        BTMusic.setupSampler()
        println("Party mode thread start!")
        if (millis() - lastSampleTime > 500) {
            lastSampleTime = millis()
            println("Amplitude: ${BTMusic.sampler?.maxAmplitude}")
        }
        while(true) {
            if (!isEngineOn()) {

                // -- DO BLINKER STUFF --

                if (millis() >= leftInidicatorCutoff && leftIndicatorOn) {
                    leftIndicatorOn = false
                    blinkerStateChange = true
                }
                // Do i turn off the right indicator?
                if (millis() >= rightIndicatorCutOff && rightIndicatorOn) {
                    rightIndicatorOn = false
                    blinkerStateChange = true
                }

                if (blinkerStateChange) {
                    blinkerStateChange = false
                    sha3.clear()
                    SAM_H_A3.set_bli_li_ein(sha3, leftIndicatorOn)
                    SAM_H_A3.set_bli_re_ein(sha3, rightIndicatorOn)
                    if (leftIndicatorOn || rightIndicatorOn) {
                        SAM_H_A3.set_hell_blink(sha3, 0xFF) // FF as we will tell SAM When to turn off
                    } else {
                        SAM_H_A3.set_hell_blink(sha3, 0x00) // Tell SAM to turn off
                    }
                    CarComm.frameQueue.addLast(sha3)
                }

                // Do lights stuff

                if (millis() >= fogCutoff && fogOn) {
                    fogOn = false
                    lightStateChange = true
                }
                // Do i turn off the right indicator?
                if (millis() >= dippedCutOff && dippedOn) {
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
                    CarComm.frameQueue.addLast(sha5)
                }
            }


            // -- Finally sleep a bit

            try {
                Thread.sleep(1)
            } catch (e: InterruptedException) {
                println("Party thread terminated by host!")
                break
            }
        }
        BTMusic.tearDownSampler()
    }

    // Check if engine is on. If it is party thread MUST exit
    fun isEngineOn() : Boolean {
        return EZS_A2.get_n_mot() != 65535 && EZS_A2.get_n_mot() != 0
    }

    fun activateLeftBlinker(durationMs: Long) {
        leftInidicatorCutoff = millis() + durationMs
        leftIndicatorOn = true
        blinkerStateChange = true
    }

    fun activateRightBlinker(durationMs: Long) {
        rightIndicatorCutOff = millis() + durationMs
        rightIndicatorOn = true
        blinkerStateChange = true
    }

    fun activateFog(durationMs: Long) {
        fogCutoff = millis() + durationMs
        fogOn = true
        lightStateChange = true
    }

    fun activateDipped(durationMs: Long) {
        dippedCutOff = millis() + durationMs
        dippedOn = true
        lightStateChange = true
    }

    fun activateHazards(durationMs: Long) {
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