package com.rndash.mbheadunit.canData.canC

import com.rndash.mbheadunit.CarCanFrame
import com.rndash.mbheadunit.canData.DataFrame
import com.rndash.mbheadunit.canData.DataSignal
import com.rndash.mbheadunit.canData.UnVerifiedValue
import java.lang.Integer.min
import kotlin.math.sign

@ExperimentalUnsignedTypes
class MS308 : DataFrame() {
    override val name : String = "MS308"
    override val dlc: Int = 8
    override val id: Int = 0x0308
    override val signals: List<DataSignal> = listOf(
            DataSignal(0, "KPL", 0, 1),
            DataSignal(0, "KUEB_O_A", 1, 1),
            DataSignal(0, "N_MAX_BG", 2, 1),
            DataSignal(0, "SAST", 3, 1),
            DataSignal(0, "SASV", 4, 1),
            DataSignal(0, "KSF_KL", 5, 1),
            DataSignal(0, "WKS_KL", 6, 1),
            DataSignal(0, "ZASBED", 7, 1),
            DataSignal(0, "NMOT", 8, 16),
            DataSignal(0, "ELHP_WARN", 25, 1),
            DataSignal(0, "EOH", 26, 1),
            DataSignal(0, "LUFI_KL", 27, 1),
            DataSignal(0, "VGL_KL", 28, 1),
            DataSignal(0, "OEL_KL", 29, 1),
            DataSignal(0, "DIAG_KL", 30, 1),
            DataSignal(0, "TANK_KL", 31, 1),
            DataSignal(0, "UEHITZ", 32, 1),
            DataSignal(0, "ZAS", 33, 1),
            DataSignal(0, "ADR_KL", 34, 1),
            DataSignal(0, "ADR_DEF_KL", 35, 1),
            DataSignal(0, "ANL_LFT", 36, 1),
            DataSignal(0, "LUEFT_MOT_KL", 37, 1),
            DataSignal(0, "DBAA", 38, 1),
            DataSignal(0, "TEMP_KL", 39, 1),
            DataSignal(0, "T_OEL", 40, 8),
            DataSignal(0, "OEL_FS", 48, 8),
            DataSignal(0, "OEL_QUAL", 56, 8)
    )

    /**
     * Returns if the speed limiter function is activated
     */
    fun isSpeedLimitActive() : Boolean = signals[2].getValue() != 0

    /**
     * Returns if engine RPM limiter has been hit due to over revving
     */
    fun isLimiterHit() : Boolean = signals[3].getValue() != 0 || signals[4].getValue() != 0

    /**
     * Returns if the Fuel filter is clogged
     */
    fun isFuelFilterClogged() : Boolean = signals[5].getValue() != 0

    /**
     * Returns if there is water in the fuel system
     * (USA based Diesels ONLY)
     */
    fun isWaterInFuel() : Boolean = signals[6].getValue() != 0

    /**
     * Returns current Engine RPM
     */
    fun getEngineRPM() : Int = signals[8].getValue()

    /**
     * Returns if there is ethanol in the fuel system, and the ECU
     * has detected it
     */
    fun isEthanolUsed() : Boolean = signals[10].getValue() != 0

    /**
     * Returns if the Engine air filter is clogged
     * (Diesels only)
     */
    fun isAirFilterClogged() : Boolean = signals[11].getValue() != 0

    /**
     * Returns if the engine is warming up the glow plugs
     * (Diesels only)
     */
    fun isPreGlowActive() : Boolean = signals[12].getValue() != 0

    /**
     * Returns if the oil pressure is too low
     */
    fun isOilPressureLow() : Boolean = signals[13].getValue() != 0

    /**
     * Returns if the fuel tank flap is open or not
     */
    fun isTankOpen() : Boolean = signals[15].getValue() != 0

    /**
     * Returns if the engine oil temperature is too high
     */
    fun isOilTempTooHigh() : Boolean = signals[16].getValue() != 0

    /**
     * Returns if the engine is cranking (Starting)
     */
    fun isCranking() : Boolean = signals[20].getValue() != 0

    /**
     * Returns if the Speed limiter display should be active on the cluster
     */
    fun isSpeedLimitDisplayActive() : Boolean = signals[22].getValue() != 0

    /**
     * Returns if the engine coolant temperature is too high
     */
    fun isCoolantTempTooHigh() : Boolean = signals[23].getValue() != 0

    /**
     * Returns the oil temperature in Celsius
     */
    fun getOilTemp() : Int = signals[23].getValue()

    /**
     * Returns the engine Oil level
     * UNKNOWN Value conversion!
     */
    @UnVerifiedValue
    fun getOilLevel() : Int = signals[24].getValue()
}