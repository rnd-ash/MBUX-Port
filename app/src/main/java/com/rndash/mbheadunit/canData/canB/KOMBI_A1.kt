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
class KOMBI_A1 : ECUFrame() {
    override val name: String = "KOMBI_A1"
    override val dlc: Int = 8
    override val id: Int = 0x000C

    /*
    MSG NAME: KL_58D_B - (%) (%) Brightness meter lighting, OFFSET 0, LENGTH 8
	MSG NAME: V_SIGNAL - (km/h) (Km / h) vehicle speed, OFFSET 8, LENGTH 8
	MSG NAME: DZ_EIN - Roof sign on (taxi), OFFSET 16, LENGTH 1
	MSG NAME: TFSM_B - Tank level minimum, OFFSET 17, LENGTH 1
	MSG NAME: AUTO_TUER - automatic door lock, OFFSET 18, LENGTH 1
	MSG NAME: T_C - temperature unit, OFFSET 19, LENGTH 1
	MSG NAME: TFL_EIN - daytime running lights, OFFSET 20, LENGTH 1
	MSG NAME: ANH_UEBW - Switch trailer monitoring, OFFSET 21, LENGTH 1
	MSG NAME: SCHLUE_ABH_EIN - Key dependence a, OFFSET 22, LENGTH 1
	MSG NAME: SP_PARK_SPERR - Mirror in park, OFFSET 23, LENGTH 1
	MSG NAME: ESH_POS_SP - Seat longitudinal position for I / O help save, OFFSET 24, LENGTH 1
	MSG NAME: SP_ANKL_SPERR - Spiegelanklappen at Fzg. lock, OFFSET 25, LENGTH 1
	MSG NAME: ESH_POS_STD - Sitzverstellweg at I / O help to default, OFFSET 28, LENGTH 1
	MSG NAME: ESH_SITZ_EIN - Seat adjustment with I / O help a, OFFSET 29, LENGTH 1
	MSG NAME: ESH_LENK_EIN - Adjustable steering column for I / O help a, OFFSET 30, LENGTH 1
	MSG NAME: ESH_AUTO_EIN - Easy Entry / automatic. Positionierg. on, OFFSET 31, LENGTH 1
	MSG NAME: SLF - search, OFFSET 32, LENGTH 1
	MSG NAME: RR_KM - Trip computer unit distance, OFFSET 33, LENGTH 1
	MSG NAME: FL_OK - High beam switch permits, OFFSET 34, LENGTH 1
	MSG NAME: UFB_EIN - Ambient lighting a, OFFSET 35, LENGTH 1
	MSG NAME: SPRACHE - language, OFFSET 36, LENGTH 4
	MSG NAME: STHL_EIN_KOMBI - / Off auxiliary heating independent ventilation, OFFSET 40, LENGTH 1
	MSG NAME: VWZ_AKT - Preset time is enabled (LED on), OFFSET 41, LENGTH 1
	MSG NAME: VWZ_AUS_MFL - Preselection time off (from LED) over MFL, OFFSET 42, LENGTH 1
	MSG NAME: IRS_VDK_EIN - Interior protection with the roof one, OFFSET 46, LENGTH 1
	MSG NAME: RDK_AKT - enable RDK, OFFSET 47, LENGTH 1
	MSG NAME: INLI_NLZ - (s) (S) Interior lighting afterglow, OFFSET 48, LENGTH 8
	MSG NAME: ABL_NLZ - (s) (S) standing or fog light afterglow (SWA), OFFSET 56, LENGTH 8
     */
    override val signals: List<FrameSignal> = listOf(
        FrameSignal("KL_58D_B", 0, 8),
        FrameSignal("V_SIGNAL", 8, 8),
        FrameSignal("DZ_EIN", 16, 1),
        FrameSignal("TFSM_B", 17, 1),
        FrameSignal("AUTO_TUER", 18, 1),
        FrameSignal("T_C", 19, 1),
        FrameSignal("TFL_EIN", 20, 1),
        FrameSignal("ANH_UEBW", 21, 1),
        FrameSignal("SCHLUE_ABH_EIN", 22, 1),
        FrameSignal("SP_PARK_SPERR", 23, 1),
        FrameSignal("ESH_POS_SP", 24, 1),
        FrameSignal("SP_ANKL_SPERR", 25, 1),
        FrameSignal("ESH_POS_STD", 28, 1),
        FrameSignal("ESH_SITZ_EIN", 29, 1),
        FrameSignal("ESH_LENK_EIN", 30, 1),
        FrameSignal("ESH_AUTO_EIN", 31, 1),
        FrameSignal("SLF", 32, 1),
        FrameSignal("RR_KM", 33, 1),
        FrameSignal("FL_OK", 34, 1),
        FrameSignal("UFB_EIN", 35, 1),
        FrameSignal("SPRACHE", 36, 4),
        FrameSignal("STHL_EIN_KOMBI", 40, 1),
        FrameSignal("VWZ_AKT", 41, 1),
        FrameSignal("VWZ_AUS_MFL", 42, 1),
        FrameSignal("IRS_VDK_EIN", 46, 1),
        FrameSignal("RDK_AKT", 47, 1),
        FrameSignal("INLI_NLZ", 48, 8),
        FrameSignal("ABL_NLZ", 56, 8)
    )

    fun getDisplayBrightness(): Int = signals[0].getValue()
    fun getSpeedKmh() : Int = signals[1].getValue()
    fun isLowFuel(): Boolean = signals[3].getValue() != 0
    fun hasAutoDoorLock(): Boolean = signals[4].getValue() != 0


}