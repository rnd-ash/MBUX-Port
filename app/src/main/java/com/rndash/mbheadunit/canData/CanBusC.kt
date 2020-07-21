package com.rndash.mbheadunit.canData

import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.canData.canC.*

@ExperimentalUnsignedTypes
object CanBusC  {
    var ms608 = MS608()
    var ms308 = MS308()

    fun updateFrames(incoming: CarCanFrame) {
        when(incoming.canID) {
            ms308.id -> ms308.parseFrame(incoming)
            ms608.id -> ms608.parseFrame(incoming)
        }
    }
}