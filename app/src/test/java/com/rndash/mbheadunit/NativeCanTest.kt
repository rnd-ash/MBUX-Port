package com.rndash.mbheadunit

import org.junit.Test

class NativeCanTest {
    companion object {
        init {
            System.loadLibrary("canbus")
        }
    }
    val n = CanbusNative()



    @Test
    fun testNative() {
        n.addBytes(byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05))
    }
}