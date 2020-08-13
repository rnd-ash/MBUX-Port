package com.rndash.mbheadunit

import android.util.Log
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.lang.Exception

@ExperimentalStdlibApi
class SerialManager() : SerialInputOutputManager.Listener {
    override fun onNewData(data: ByteArray?) {
        // Add to our native buffer
        data?.let {
            CarComm.nativeCanbus.sendBytesToBuffer(it, it.size)
        }
    }

    override fun onRunError(e: Exception?) {
    }
}