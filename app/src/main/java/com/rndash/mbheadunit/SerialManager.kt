package com.rndash.mbheadunit

import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.lang.Exception

@ExperimentalStdlibApi
class SerialManager() : SerialInputOutputManager.Listener {

    companion object {
        var buffer: ArrayDeque<Byte> = ArrayDeque(0)
    }

    override fun onNewData(data: ByteArray?) {
        data?.let { b -> b.forEach { buffer.add(it) } }
    }

    override fun onRunError(e: Exception?) {
    }
}