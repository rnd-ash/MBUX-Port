package com.rndash.mbheadunit.canData.canB

import android.content.Intent
import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.canData.CanBusB
import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class KOMBI_A5 : ECUFrame() {
    override val name: String = "KOMBI_A5"
    override val dlc: Int = 4 // Full frame is 8, my unit only car 5 bytes??
    override val id: Int = 0x01CA

    override val signals: List<FrameSignal> = listOf(
            FrameSignal("KI_STAT", 0, 8),
            FrameSignal("BUTTON_4_2", 8, 1),
            FrameSignal("BUTTON_4_1", 9, 1),
            FrameSignal("BUTTON_3_2", 10, 1),
            FrameSignal("BUTTON_3_1", 11, 1),
            FrameSignal("BUTTON_2_2", 12, 1),
            FrameSignal("BUTTON_2_1", 13, 1),
            FrameSignal("BUTTON_1_2", 14, 1),
            FrameSignal("BUTTON_1_1", 15, 1),
            FrameSignal("BUTTON_8_2", 16, 1),
            FrameSignal("BUTTON_8_1", 17, 1),
            FrameSignal("BUTTON_7_2", 18, 1),
            FrameSignal("BUTTON_7_1", 19, 1),
            FrameSignal("BUTTON_6_2", 20, 1),
            FrameSignal("BUTTON_6_1", 21, 1),
            FrameSignal("BUTTON_5_2", 22, 1),
            FrameSignal("BUTTON_5_1", 23, 1),
            FrameSignal("PTT_4_2", 24, 1),
            FrameSignal("PTT_4_1", 25, 1),
            FrameSignal("PTT_3_2", 26, 1),
            FrameSignal("PTT_3_1", 27, 1),
            FrameSignal("PTT_2_2", 28, 1),
            FrameSignal("PTT_2_1", 29, 1),
            FrameSignal("PTT_1_2", 30, 1),
            FrameSignal("PTT_1_1", 31, 1)
    )

    override fun parseFrame(frame: CarCanFrame) {
        super.parseFrame(frame)
        // Now check what page we are on, and what Keys are being pressed
        val tmp = ArrayList<CanBusB.WheelKey>()
        if (signals[1].getValue() != 0) tmp.add(CanBusB.WheelKey.TEL_DN)
        if (signals[2].getValue() != 0) tmp.add(CanBusB.WheelKey.TEL_UP)
        if (signals[3].getValue() != 0) tmp.add(CanBusB.WheelKey.VOL_DN)
        if (signals[4].getValue() != 0) tmp.add(CanBusB.WheelKey.VOL_UP)
        if (signals[7].getValue() != 0) tmp.add(CanBusB.WheelKey.ARR_DN)
        if (signals[8].getValue() != 0) tmp.add(CanBusB.WheelKey.ARR_UP)
        if(signals[0].getValue() != 0) {
            CanBusB.ic_page = when(signals[0].getValue()) {
                3 -> CanBusB.KombiPage.AUDIO
                5 -> CanBusB.KombiPage.TELEPHONE
                else -> CanBusB.KombiPage.OTHER
            }
        }
        CanBusB.curr_keys = tmp
    }
}