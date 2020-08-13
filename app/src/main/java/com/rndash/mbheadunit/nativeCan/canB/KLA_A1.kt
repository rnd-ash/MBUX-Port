package com.rndash.mbheadunit.nativeCan.canB

import com.rndash.mbheadunit.nativeCan.CanBAddrs
import com.rndash.mbheadunit.nativeCan.CanBusNative


object KLA_A1 {

    /* Switch on heated rear window */
    fun get_hhs_ein() : Boolean = getParam(0, 1) != 0

    /* EC mode active */
    fun get_ec_akt() : Boolean = getParam(1, 1) != 0

    /* Switch internal sensor Fan */
    fun get_ifg_ein() : Boolean = getParam(2, 1) != 0

    /* Switch on auxiliary water pump */
    fun get_zwp_ein() : Boolean = getParam(3, 1) != 0

    /* Heater switch allows */
    fun get_zh_ein_ok() : Boolean = getParam(4, 1) != 0

    /* Idle speed increase for the cooling capacity increase */
    fun get_ll_dza() : Boolean = getParam(5, 1) != 0

    /* heating heater */
    fun get_heizen() : Boolean = getParam(6, 1) != 0

    /* Heater vent */
    fun get_lueften() : Boolean = getParam(7, 1) != 0

    /* (%) (%) Motor fan setpoint speed */
    fun get_nlfts() : Int = getParam(8, 8)

    /* (Nm) (Nm) torque absorption chiller */
    fun get_m_komp() : Int = getParam(16, 8)

    /* (%) (%) Refrigeration compressor control signal */
    fun get_komp_stell() : Int = getParam(24, 8)

    /* Frontscheibenhzg. Switch (for G463) */
    fun get_fsb_hzg_ein() : Boolean = getParam(32, 1) != 0

    /* Switching point increase in cooling power deficit */
    fun get_g_anf_kuehl_kla() : Boolean = getParam(33, 1) != 0

    /* (%) (%) Fan power */
    fun get_geb_lstg() : Int = getParam(40, 8)

    /* convection active */
    fun get_ul_akt_kla() : Boolean = getParam(48, 1) != 0

    /* Switching point increase in heating power deficit */
    fun get_g_anf_kla() : Boolean = getParam(49, 1) != 0

    /* Position ventilation flap above */
    fun get_lko_vorn() : Int = getParam(50, 2)

    /* Position ventilation flap center */
    fun get_lkm_vorn() : Int = getParam(52, 2)

    /* Position ventilation flap down */
    fun get_lku_vorn() : Int = getParam(54, 2)

    /* (°C) (° C) indoor temperature */
    fun get_t_innen_kla() : Int = getParam(56, 8)



    override fun toString(): String {
        return """
                HHS_EIN: ${get_hhs_ein()}
                EC_AKT: ${get_ec_akt()}
                IFG_EIN: ${get_ifg_ein()}
                ZWP_EIN: ${get_zwp_ein()}
                ZH_EIN_OK: ${get_zh_ein_ok()}
                LL_DZA: ${get_ll_dza()}
                HEIZEN: ${get_heizen()}
                LUEFTEN: ${get_lueften()}
                NLFTS: ${get_nlfts()}
                M_KOMP: ${get_m_komp()}
                KOMP_STELL: ${get_komp_stell()}
                FSB_HZG_EIN: ${get_fsb_hzg_ein()}
                G_ANF_KUEHL_KLA: ${get_g_anf_kuehl_kla()}
                GEB_LSTG: ${get_geb_lstg()}
                UL_AKT_KLA: ${get_ul_akt_kla()}
                G_ANF_KLA: ${get_g_anf_kla()}
                LKO_VORN: ${get_lko_vorn()}
                LKM_VORN: ${get_lkm_vorn()}
                LKU_VORN: ${get_lku_vorn()}
                T_INNEN_KLA: ${get_t_innen_kla()}
        """.trimIndent()
    }

    private fun getParam(o: Int, l: Int) : Int = CanBusNative.getECUParameterB(CanBAddrs.KLA_A1, o, l)
}