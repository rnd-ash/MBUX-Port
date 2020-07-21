package com.rndash.mbheadunit.canData

import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.canData.canB.KLA_A1
import com.rndash.mbheadunit.canData.canB.SAM_V_A2
import com.rndash.mbheadunit.canData.canC.*

@ExperimentalUnsignedTypes
object CanBusB  {
    var kla_a1 = KLA_A1()
    var sam_v_a2 = SAM_V_A2()

    fun updateFrames(incoming: CarCanFrame) {
        when(incoming.canID) {
            kla_a1.id -> kla_a1.parseFrame(incoming)
            sam_v_a2.id -> sam_v_a2.parseFrame(incoming)
        }
    }
}