package com.rndash.mbheadunit

import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.nio.ByteBuffer

open class IOManager(readBufSize: Int, private val serialPort: UsbSerialPort, listener: Listener) : SerialInputOutputManager(serialPort, listener) {
    enum class ManagerState {
        STOPPED,
        RUNNING,
        STOPPING
    }
    private var state: ManagerState = ManagerState.STOPPED
    private val readBuffer = ByteBuffer.allocate(readBufSize)
    private val writeBuffer = ByteBuffer.allocate(4096)
    override fun getState(): State {
        return when(state) {
            ManagerState.STOPPED -> State.STOPPED
            ManagerState.RUNNING -> State.RUNNING
            ManagerState.STOPPING -> State.STOPPING
        }
    }


    override fun run() {
        synchronized(this) {
            check(state == ManagerState.STOPPED) { "Already running" }
            state = ManagerState.RUNNING
        }

        Log.i("IOManager", "Running ...")
        try {
            while (true) {
                if (state != ManagerState.RUNNING) {
                    Log.i(
                        "IOManager",
                        "Stopping mState=$state"
                    )
                    break
                }
                step()
            }
        } catch (e: Exception) {
            Log.w(
                "IOManager",
                "Run ending due to exception: " + e.message,
                e
            )
            val listener = listener
            listener?.onRunError(e)
        } finally {
            synchronized(this) {
                state = ManagerState.STOPPED
                Log.i("IOManager", "Stopped")
            }
        }
    }

    fun step() {
        // Handle incoming data.

        // Handle incoming data.
        var len = serialPort.read(readBuffer.array(), 100)
        if (len > 0) {
            val listener = listener
            if (listener != null) {
                val data = ByteArray(len)
                readBuffer[data, 0, len]
                listener.onNewData(data)
            }
            readBuffer.clear()
        }

        // Handle outgoing data.

        // Handle outgoing data.
        var outBuff: ByteArray? = null
        synchronized(writeBuffer) {
            len = writeBuffer.position()
            if (len > 0) {
                outBuff = ByteArray(len)
                writeBuffer.rewind()
                writeBuffer[outBuff, 0, len]
                writeBuffer.clear()
            }
        }
        if (outBuff != null) {
            serialPort.write(outBuff, 100)
        }
    }
}