package com.rndash.mbheadunit

import com.rndash.mbheadunit.canData.canB.kombiDisplay.ICDefines
import com.rndash.mbheadunit.canData.canB.kombiDisplay.ICDisplay
import com.rndash.mbheadunit.canData.canB.kombiDisplay.ISO15765Protocol
import org.junit.Test

class ISOTest {

    @ExperimentalStdlibApi
    @ExperimentalUnsignedTypes
    @Test
    fun testSinglePacket() {
        ICDisplay().sendHeader(ICDefines.Page.AUDIO, ICDefines.TextFormat.RIGHT_JUSTIFICATION, "AUX ")
    }
}