package com.rndash.mbheadunit.doom

import android.view.Display
import com.rndash.mbheadunit.nativeCan.KombiDisplay

class DisplayText(var header: String, var body: String) {
    var queueChange = true
}

/**
 * Controls what is on the IC display whilst playing DOOM
 */
object DisplayManager {
    var displays = arrayOf(
        DisplayText("Weapon", "None"),
        DisplayText("Ammo", "0"),
        DisplayText("Health", "100%"),
        DisplayText("Armour", "0%")
    )
    var currIndex = 0

    fun setDisplayBody(obj: DisplayText, newText: String) {
        if (newText != obj.body) {
            obj.queueChange = true
            obj.body = newText
        }
    }

    fun setWeapon(name: String) {
        setDisplayBody(displays[0], name)
    }

    fun setAmmo(value: Int) {
        setDisplayBody(displays[1], "$value")
    }

    fun setHealth(perc: Int) {
        setDisplayBody(displays[2], "$perc%")
    }

    fun setArmour(perc: Int) {
        setDisplayBody(displays[3], "$perc%")
    }

    fun changeMode(up: Boolean) {
        var newIdx = if(up) {
            currIndex - 1
        } else {
            currIndex + 1
        }
        if (newIdx >= displays.size) {
            newIdx = 0
        }
        if (newIdx < 0) {
            newIdx = displays.size - 1
        }
        currIndex = newIdx
    }

    private val updaterThread = Thread() {
        var currDispIdx = currIndex
        var firstRun = true
        while(true) {
            if (currDispIdx != currIndex || firstRun) {
                currDispIdx = currIndex
                firstRun = false
                KombiDisplay.setAudioHeaderText(displays[currDispIdx].header, arrayOf(KombiDisplay.TEXT_FMT.RIGHT_JUSTIFIED))
                displays[currDispIdx].queueChange = false
                Thread.sleep(150)
                KombiDisplay.setAudioBodyText(displays[currDispIdx].body, arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
            }
            if (displays[currDispIdx].queueChange) {
                displays[currDispIdx].queueChange = false
                KombiDisplay.setAudioBodyText(displays[currDispIdx].body, arrayOf(KombiDisplay.TEXT_FMT.CENTER_JUSTIFIED))
            }
            Thread.sleep(100)
        }
    }

    fun init() {
        updaterThread.start()
    }

    fun kill() {
        updaterThread.interrupt()
    }
}