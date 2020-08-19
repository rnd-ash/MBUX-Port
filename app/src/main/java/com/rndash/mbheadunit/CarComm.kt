package com.rndash.mbheadunit

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Process
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.rndash.mbheadunit.nativeCan.CanBusNative
import java.util.concurrent.Executors


@ExperimentalStdlibApi
class CarComm(device: UsbDevice, manager: UsbManager) {
    enum class CANBUS_ID(val id: Char) {
        CANBUS_B('B'),
        CANBUS_C('C')
    }

    companion object {
        private var serialDevice : UsbSerialPort? = null
        private val SERIAL_INPUT_OUTPUT_MANAGER_THREAD_PRIORITY: Int = Process.THREAD_PRIORITY_URGENT_AUDIO
    }


    // This thread polls for can frames from JNI. If a frame is available, it is sent to arduino
    private val frameQueue = ArrayDeque<CanFrame>()
    val sendThread = Thread {
        var hasFrame = false
        while(true) {
            // Check JVM frame queue
            if (frameQueue.isNotEmpty()) {
                serialDevice?.write(frameQueue.removeFirst().toStruct(), 100)
                hasFrame = true
            }
            // Check Native frame queue (Used by AGW<->IC)
            CanBusNative.getSendFrame()?.let {
                serialDevice?.write(it, 100)
                hasFrame = true
            }
            if (!hasFrame) {
                Thread.sleep(1)
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
        CanBusNative.init()
        port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        serialDevice = port
        val readThread = IOManager(64, serialDevice!!)
        // Start polling for data
        Executors.newSingleThreadExecutor().submit(readThread)
        // Start polling for frames to be sent
        sendThread.start()
        // Send the start signal to arduino
    }

    fun quit() {
        println("Destroying serial")
        serialDevice?.close()
        sendThread.interrupt()
        CanBusNative.destroy()
    }
}
