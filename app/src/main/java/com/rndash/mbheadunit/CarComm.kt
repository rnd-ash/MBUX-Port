package com.rndash.mbheadunit

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.rndash.mbheadunit.canData.CanBusB
import com.rndash.mbheadunit.canData.CanBusC
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
        var read: Byte = 0x00
        while(true) {
            if (SerialManager.buffer.size != 0) {
                try {
                    read = SerialManager.buffer.removeFirst()
                    if (read == 0x0A.toByte()) {
                        if (str.length > 7) {
                            processFrame(str)
                        }
                        str = ""
                    } else {
                        str += read.toChar()
                    }
                } catch (e: NullPointerException){}
            }
        }
    }

    fun processFrame(str: String) {
        when(str[0]) {
            'B' -> {
                CarCanFrame.fromHexStr(str.drop(1))?.let {
                    CanBusB.updateFrames(it)
                }
            }
            'C' -> {
                CarCanFrame.fromHexStr(str.drop(1))?.let {
                    CanBusC.updateFrames(it)
                }
            }
        }
    }

    companion object {
        private var serialDevice : UsbSerialPort? = null
        @Synchronized
        fun sendFrame(targetBus: CANBUS_ID, canFrame: CarCanFrame) {
            if (serialDevice == null) {
                Log.e("SerialSend", "Sending without Arduino present!")
            }
            println("Sending $canFrame")
            var bytes = byteArrayOf(targetBus.id.toByte())
            bytes += canFrame.canID.toByte()
            bytes += (canFrame.canID shr 8 and 0xFF).toByte()
            bytes += canFrame.dlc.toByte()
            bytes += canFrame.data.toByteArray()
            serialDevice?.write( bytes , 100)
        }
    }

    var buff: ArrayDeque<Byte> = ArrayDeque(0)
    var isModify = false

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
    }

    fun quit() {
        println("Destroying serial")
        serialDevice?.close()
        readThread.interrupt()
    }
}
