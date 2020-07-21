package com.rndash.mbheadunit

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.rndash.mbheadunit.canData.CanBusB
import com.rndash.mbheadunit.canData.CanBusC
import com.rndash.mbheadunit.canData.canB.KLA_A1
import com.rndash.mbheadunit.canData.canB.SAM_V_A2


@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class CarComm(device: UsbDevice, manager: UsbManager) {

    enum class CANBUS_ID(val id: Char) {
        CANBUS_B('B'),
        CANBUS_C('C')
    }

    companion object {
        private var serialDevice : UsbSerialPort? = null
        fun sendFrame(targetBus: CANBUS_ID, canFrame: CarCanFrame) {
            serialDevice!!.write( byteArrayOf(targetBus.id.toByte()) + canFrame.toByteArray().toByteArray() , 100)
        }
    }

    val readThread = Thread {
        var buff: ByteArray = byteArrayOf()
        serialDevice!!.dtr = false
        serialDevice!!.purgeHwBuffers(true, true);
        serialDevice!!.dtr = true
        val readBuffer = ByteArray(1024)

        while(true) {
            val readCount = serialDevice!!.read(readBuffer, 100)
            (buff+readBuffer.take(readCount)).decodeToString().split("\n").apply {
                buff = this.last().toByteArray()
                this.forEach { line ->
                    if (line.length < 7) {
                        return@forEach
                    }
                    when(line[0]) {
                        'B' -> {
                            CarCanFrame.fromHexStr(line.drop(1))?.let {
                                if (it.canID == 0x0030) {
                                    println(KLA_A1().apply { this.parseFrame(it) })
                                }
                                CanBusB.updateFrames(it)
                            }
                        }
                        'C' -> {
                            CarCanFrame.fromHexStr(line.drop(1))?.let {
                                CanBusC.updateFrames(it)
                            }
                        }
                        else -> {}
                    }
                }
            }
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
        readThread.start()
    }

    fun quit() {
        println("Destroying serial")
        readThread.interrupt()
        serialDevice?.close()
    }
}
