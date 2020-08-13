package com.rndash.mbheadunit

import android.util.Log
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.rndash.mbheadunit.nativeCan.CanBusNative
import java.lang.Exception

@ExperimentalStdlibApi
class SerialManager() : SerialInputOutputManager.Listener {
    override fun onNewData(data: ByteArray?) {
        // Add to our native buffer
        data?.let {
            CanBusNative.sendBytesToBuffer(it, it.size)
        }
    }

    override fun onRunError(e: Exception?) {
    }
}