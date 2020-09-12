package com.rndash.mbheadunit.doom

import android.util.Log

class Logger {
    companion object {
        fun logInfo(tag: String, msg: String) = Log.i("DOOM-$tag", msg)
        fun logWarn(tag: String, msg: String) = Log.w("DOOM-$tag", msg)
        fun logError(tag: String, msg: String) = Log.e("DOOM-$tag", msg)
        fun logDebug(tag: String, msg: String) = Log.d("DOOM-$tag", msg)
    }
}