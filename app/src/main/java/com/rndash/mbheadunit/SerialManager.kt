package com.rndash.mbheadunit

import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.lang.Exception

@ExperimentalStdlibApi
class SerialManager() : SerialInputOutputManager.Listener {

    companion object {
        val buffer: ArrayDeque<Byte> = ArrayDeque(0)
    }

    override fun onNewData(data: ByteArray?) {
        data?.let {
            synchronized(buffer) {
                it.forEach { b -> buffer.add(b) }
            }
        }
    }

    override fun onRunError(e: Exception?) {
    }
}