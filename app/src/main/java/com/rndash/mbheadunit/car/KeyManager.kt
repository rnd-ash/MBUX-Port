package com.rndash.mbheadunit.car

import android.os.Looper
import com.rndash.mbheadunit.nativeCan.KombiDisplay
import com.rndash.mbheadunit.nativeCan.canB.KI_STAT
import com.rndash.mbheadunit.nativeCan.canB.KOMBI_A5
import java.security.Key
import kotlin.properties.Delegates

object KeyManager {
    var LONG_PRESS_THRESHOLD_MS = 1000

    // Simplified KI_STAT Page
    enum class PAGE {
        AUDIO, // Audio page on IC
        TELEPHONE, // Telephone page on IC
        OTHER // Some other page I don't care about
    }

    enum class KEY {
        TEL_ANSWER, // Telephone answer button
        TEL_DECLINE, // Telephone decline button
        VOLUME_UP, // Volume up button
        VOLUME_DOWN, // Volume down button
        PAGE_UP, // Up arrow
        PAGE_DOWN // Down arrow
    }

    interface KeyListener {
        fun onShortPress(pg: PAGE)
        fun onLongPress(pg: PAGE)
    }

    private var isIdle = false
    private var simplePage: PAGE = PAGE.OTHER
    private var page: KI_STAT by Delegates.observable(KI_STAT.RESERVED) { _, o, n ->
        if (o != n) {
            println("Cluster page: $n")
            if (n != KI_STAT.RESERVED) {
                KombiDisplay.setPage(page.raw.toByte())
            }
            simplePage = when(n) {
                KI_STAT.RESERVED -> simplePage // Itself if reserved
                KI_STAT.AUDIO -> PAGE.AUDIO
                KI_STAT.TEL -> PAGE.TELEPHONE
                else -> PAGE.OTHER
            }
        }
    }

    private var volUp = KeyState()
    private var volDown = KeyState()
    private var telUp = KeyState()
    private var telDown = KeyState()
    private var pgUp = KeyState()
    private var pgDown = KeyState()

    fun registerPageUpListener(key: KEY, l: KeyListener) {
        when(key) {
            KEY.PAGE_UP -> pgUp.registerListener(l)
            KEY.PAGE_DOWN -> pgDown.registerListener(l)
            KEY.TEL_ANSWER -> telUp.registerListener(l)
            KEY.TEL_DECLINE -> telDown.registerListener(l)
            KEY.VOLUME_UP -> volUp.registerListener(l)
            KEY.VOLUME_DOWN -> volDown.registerListener(l)
        }
    }

    val watcher = Thread() {
        while(true) {
            KOMBI_A5.get_ki_stat().let {
                page = it
                if (it == KI_STAT.RESERVED && !isIdle) { // Key released
                    isIdle = true
                    volUp.release(simplePage)
                    volDown.release(simplePage)
                    telUp.release(simplePage)
                    telDown.release(simplePage)
                    pgUp.release(simplePage)
                    pgDown.release(simplePage)

                } else if (it != KI_STAT.RESERVED) {
                    isIdle = false
                    if (KOMBI_A5.get_button_1_1()) { pgUp.press(simplePage) }
                    if (KOMBI_A5.get_button_1_2()) { pgDown.press(simplePage) }
                    if (KOMBI_A5.get_button_3_1()) { volUp.press(simplePage) }
                    if (KOMBI_A5.get_button_3_2()) { volDown.press(simplePage) }
                    if (KOMBI_A5.get_button_4_1()) { telUp.press(simplePage) }
                    if (KOMBI_A5.get_button_4_2()) { telDown.press(simplePage) }
                }
            }
            Thread.sleep(25)
        }
    }
}