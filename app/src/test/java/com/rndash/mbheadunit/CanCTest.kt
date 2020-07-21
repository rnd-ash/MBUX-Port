package com.rndash.mbheadunit

import com.rndash.mbheadunit.canData.DataFrame
import com.rndash.mbheadunit.canData.DataSignal
import com.rndash.mbheadunit.canData.canC.MS608
import org.junit.Test

@ExperimentalUnsignedTypes
class CanCTest {
    @Test
    fun testMS608() {
        var x = CarCanFrame(0x0608, arrayOf(129,63,6,45,250,5,191,0))
        var f = MS608()
        f.parseFrame(x)
        println(f)
        f.parseFrame(f.createCanFrame())
        println(f)
    }
}