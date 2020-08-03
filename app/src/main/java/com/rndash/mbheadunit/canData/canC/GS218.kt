package com.rndash.mbheadunit.canData.canC

import com.rndash.mbheadunit.canData.ECUFrame
import com.rndash.mbheadunit.canData.FrameSignal
import com.rndash.mbheadunit.canData.UnVerifiedValue
import kotlin.math.sign

@ExperimentalUnsignedTypes
class GS218 : ECUFrame() {

    override val name : String = "GS218"
    override val dlc: Int = 8
    override val id: Int = 0x0218

    enum class TCState {
        OPEN,
        SLIPPING,
        LOCKED
    }

    /*
    MSG NAME: MTGL_EGS - Motormomentenanf. Toggle 40ms + -10, OFFSET 0, LENGTH 1
	MSG NAME: MMIN_EGS - Engine torque requirement Min, OFFSET 1, LENGTH 1
	MSG NAME: MMAX_EGS - Engine torque requirement Max, OFFSET 2, LENGTH 1
	MSG NAME: M_EGS - Geford. engine torque, OFFSET 3, LENGTH 13
	MSG NAME: GZC - Target gear, OFFSET 16, LENGTH 4
	MSG NAME: GIC - Actual gear, OFFSET 20, LENGTH 4
	MSG NAME: K_S_B - Best. (Wandlerüberbrück.-) clutch "slipping", OFFSET 24, LENGTH 1
	MSG NAME: K_O_B - Best. (Wandlerüberbrück.-) Clutch "open", OFFSET 25, LENGTH 1
	MSG NAME: K_G_B - Best. (Wandlerüberbrück.-) clutch "closed", OFFSET 26, LENGTH 1
	MSG NAME: G_G - off-road gear, OFFSET 27, LENGTH 1
	MSG NAME: GSP_OK - Basic shift program O.K., OFFSET 28, LENGTH 1
	MSG NAME: FW_HOCH - Driving resistance is high, OFFSET 29, LENGTH 1
	MSG NAME: SCHALT - circuit, OFFSET 30, LENGTH 1
	MSG NAME: HSM - Manual shift mode, OFFSET 31, LENGTH 1
	MSG NAME: GET_OK - transmission ok, OFFSET 32, LENGTH 1
	MSG NAME: KS - start bang, OFFSET 33, LENGTH 1
	MSG NAME: ALF - start enabling, OFFSET 34, LENGTH 1
	MSG NAME: GS_NOTL - GS in emergency operation, OFFSET 35, LENGTH 1
	MSG NAME: UEHITZ_GET - Overtemperature gear, OFFSET 36, LENGTH 1
	MSG NAME: KD - Kick down, OFFSET 37, LENGTH 1
	MSG NAME: FPC_AAD - Driving program for AAD, OFFSET 38, LENGTH 2
	MSG NAME: MPAR_EGS - Engine torque request parity (even parity), OFFSET 40, LENGTH 1
	MSG NAME: DYN1_EGS - Engagement mode / drive torque control, OFFSET 41, LENGTH 1
	MSG NAME: DYN0_AMR_EGS - Engagement mode / drive torque control, OFFSET 42, LENGTH 1
	MSG NAME: K_LSTFR - Converter lockup clutch free of load, OFFSET 45, LENGTH 1
	MSG NAME: MOT_NAUS_CNF - MOT_NAUS-Confirmbit, OFFSET 46, LENGTH 1
	MSG NAME: MOT_NAUS - Motor Emergency Shutdown, OFFSET 47, LENGTH 1
	MSG NAME: MKRIECH - Creep (FFh at EGS, CVT) or CALID / CVN, OFFSET 48, LENGTH 8
	MSG NAME: FEHLPRF_ST - Status error checking, OFFSET 56, LENGTH 2
	MSG NAME: CALID_CVN_AKT - CALID / CVN-transmission active, OFFSET 58, LENGTH 1
	MSG NAME: FEHLER - Error number or counter for CALID / CVN transmission, OFFSET 59, LENGTH 5
     */
    override val signals: List<FrameSignal> = listOf(
            FrameSignal( "MTCL_EGS", 0, 1),
            FrameSignal("MMIN_GS", 1, 1),
            FrameSignal("MMAX_EGS", 2, 1),
            FrameSignal("M_EGS", 3, 13),
            FrameSignal("GZS", 16, 4),
            FrameSignal("GIC", 20, 4),
            FrameSignal("K_S_B", 24, 1),
            FrameSignal("K_O_B", 25, 1),
            FrameSignal("K_G_B", 26, 1),
            FrameSignal("G_G", 27, 1),
            FrameSignal("GSP_OK", 28, 1),
            FrameSignal("FW_HOCH", 29, 1),
            FrameSignal("SCHALT", 30, 1),
            FrameSignal("HSM", 31, 1),
            FrameSignal("GET_OK", 32, 1),
            FrameSignal("KS", 33, 1),
            FrameSignal("ALF", 34, 1),
            FrameSignal("GS_NOTL", 35, 1),
            FrameSignal("UEHITZ_GET", 36, 1),
            FrameSignal("KD", 37, 1),
            FrameSignal("FPC_AAD", 38, 2),
            FrameSignal("MPAR_EGS", 40, 1),
            FrameSignal("DYN1_EGS", 41, 1),
            FrameSignal("DYN0_AMR_EGS", 42, 1),
            FrameSignal("K_LSTFR", 45, 1),
            FrameSignal("MOT_NAUS_CNF", 46, 1),
            FrameSignal("MOT_NAUS", 47, 1),
            FrameSignal("MKRIECH", 48, 8),
            FrameSignal("FEHLPRT_ST", 56, 2),
            FrameSignal("CALID_CVN_AKR", 58, 1),
            FrameSignal("FEHLER", 59, 5)
    )


    fun getEngineTorque() : Int = signals[3].getValue()

    fun getTargetGear() : Int = signals[4].getValue()

    fun getActualGear() : Int = signals[5].getValue()

    fun isTCSlipping() : Boolean = signals[6].getValue() != 0

    fun isTCOpen() : Boolean = signals[7].getValue() != 0

    fun isTCClosed() : Boolean = signals[8].getValue() != 0

    fun isTransmissionOK() : Boolean = signals[10].getValue() != 0

    fun isDrivingResistanceTooHigh(): Boolean = signals[11].getValue() != 0

    fun isManualOverrideMode(): Boolean = signals[13].getValue() != 0

    fun isOverheating(): Boolean = signals[17].getValue() != 0

    fun getTCState() : TCState {
        return when {
            signals[8].getValue() != 0 -> TCState.LOCKED
            // signals[7] is 'OPEN'
            signals[6].getValue() != 0 -> TCState.SLIPPING
            else -> TCState.OPEN
        }
    }

    override fun toString(): String {
        return """
            GS218 (722.x transmission)
        """
    }
}