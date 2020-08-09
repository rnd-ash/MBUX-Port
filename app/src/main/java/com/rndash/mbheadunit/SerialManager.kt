package com.rndash.mbheadunit

import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.lang.Exception

@ExperimentalStdlibApi
class SerialManager() : SerialInputOutputManager.Listener {
    val canbusNative = CanbusNative()
    override fun onNewData(data: ByteArray?) {
        // Add to our native buffer
        data?.let { canbusNative.addBytes(it) }
    }

    override fun onRunError(e: Exception?) {
    }
}