package com.rndash.mbheadunit

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
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
        fun init_test() {
            Log.w("CARCOMM", "No arduino found - launching in test mode (assuming bench test)!")
            sendThread.start()
            CanBusNative.init()
        }

        // 1 (START OF FRAME
        // 11 (CANID STD)
        // 1 (RTR)
        // 2 (RESERVED)
        // 4 (DLC)
        // 15 (CRC)
        // 1 (CRC DELIMITER)
        // 1 (ACK SLOT)
        // 1 (ACK DELIMITER)
        // 1 (EOF)
        const val bits = 38 // Bits added to canframe that are not part of the data (Excluding bit stuffing)

        var serialDevice: UsbSerialPort? = null

        fun sendFrame(cf: CanFrame) {
            serialDevice?.write(cf.toStruct(), 100)
            txBits += cf.dlc + bits
        }

        // This thread polls for can frames from JNI. If a frame is available, it is sent to arduino
        val sendThread = Thread {
            println("Send thread started!")
            while (serialDevice == null) {
                Thread.sleep(10)
            }
            var nativeBA: ByteArray?
            while (true) {
                nativeBA = CanBusNative.getSendFrame()
                if (nativeBA != null) {
                    serialDevice?.write(nativeBA, 100)
                    // DLC*8 for bits + extra bits on the frame
                    txBits += nativeBA[3].toInt()*8 + bits
                } else {
                    Thread.sleep(5)
                }
            }
        }

        @Volatile
        internal var txBits = 0L

        fun getTxRate() : Long {
            val tmp = txBits
            txBits = 0L
            return tmp
        }

        external fun getRxRate() : Long
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
        try {
            sendThread.start()
        } catch (e: IllegalThreadStateException){}
        CanBusNative.init()
        port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        serialDevice = port
        val readThread = IOManager(64, serialDevice!!)
        // Start polling for data
        Executors.newSingleThreadExecutor().submit(readThread)
        // Start polling for frames to be sent
        // Send the start signal to arduino
    }

    fun quit() {
        println("Destroying serial")
        serialDevice?.close()
        sendThread.interrupt()
        CanBusNative.destroy()
    }
}
