package com.rndash.mbheadunit.ui

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import java.util.*

abstract class UIFragment(private val uiRefreshTime: Long) : Fragment() {
    open fun onKeyDown(code: Int, keyEvent: KeyEvent): Boolean = true

    protected var timerTask: (() -> Unit?)? = null

    private var runningTimer: Timer = Timer()

    override fun onResume() {
        super.onResume()
        runningTimer = Timer()
        timerTask?.let { runningTimer.schedule(object: TimerTask(){
            override fun run() = it.invoke()!!
        }, 0, uiRefreshTime) }
    }

    override fun onPause() {
        super.onPause()
        runningTimer.cancel()
    }
}