package com.rndash.mbheadunit.ui

import android.view.KeyEvent
import androidx.fragment.app.Fragment

abstract class UIFragment() : Fragment() {
    open fun onKeyDown(code: Int, keyEvent: KeyEvent): Boolean = true
}