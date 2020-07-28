package com.rndash.mbheadunit

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.rndash.mbheadunit.SerialManager.Companion.buffer
import com.rndash.mbheadunit.canData.CanBusB
import com.rndash.mbheadunit.canData.CanBusC
import com.rndash.mbheadunit.canData.canB.kombiDisplay.ICDisplay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import java.lang.NullPointerException
import java.util.concurrent.Executors


@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class CarComm(device: UsbDevice, manager: UsbManager) {

    enum class CANBUS_ID(val id: Char) {
        CANBUS_B('B'),
        CANBUS_C('C')
    }

    val readThread = Thread {
        var str = ""
        var b: Byte?
        while(true) {
            synchronized(buffer) {
                b = buffer.removeFirstOrNull()
            }
            b?.let {
                if (it == '\n'.toByte()){
                    processFrame(str)
                    str = ""
                } else {
                    str += it.toChar()
                }
            }
        }
    }

    private fun processFrame(str: String) {
        try {
            when (str[0]) {
                'B' -> {
                    CarCanFrame.fromHexStr(str.drop(1))?.let {
                        CoroutineScope(Dispatchers.Default).launch { CanBusB.updateFrames(it) }
                    }
                }
                'C' -> {
                    CarCanFrame.fromHexStr(str.drop(1))?.let {
                        CoroutineScope(Dispatchers.Default).launch { CanBusC.updateFrames(it) }
                    }
                }
            }
        } catch (e: IndexOutOfBoundsException){
        }
    }

    companion object {
        private var serialDevice : UsbSerialPort? = null
        @Synchronized
        fun sendFrame(targetBus: CANBUS_ID, canFrame: CarCanFrame) {
            if (serialDevice == null) {
                Log.e("SerialSend", "Sending without Arduino present!")
            }
            var bytes = byteArrayOf(targetBus.id.toByte())
            bytes += canFrame.canID.toByte()
            bytes += (canFrame.canID shr 8 and 0xFF).toByte()
            bytes += canFrame.dlc.toByte()
            bytes += canFrame.data.toByteArray()
            serialDevice?.write( bytes , 100)
        }
    }

    init {
        val availableDevices = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDevices.isEmpty()) {
            throw Exception("No valid devices found")
        } else {
            println("Found ${availableDevices.size} devices")
        }
        val driver: UsbSerialDriver = availableDevices[0]
        val connection = manager.openDevice(driver.device)
        val port = driver.ports[0]
        port.open(connection)
        port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        serialDevice = port
        val usbIoManager = SerialInputOutputManager(serialDevice, SerialManager())
        Executors.newSingleThreadExecutor().submit(usbIoManager)
        readThread.start()
        Thread() { ICDisplay.beginInitSequence() }.start() // Init the display now we are connected
    }

    fun quit() {
        println("Destroying serial")
        serialDevice?.close()
        readThread.interrupt()
    }
}
