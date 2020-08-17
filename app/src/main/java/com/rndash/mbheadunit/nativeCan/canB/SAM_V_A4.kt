
@file:Suppress("unused", "FunctionName")
package com.rndash.mbheadunit.nativeCan.canB
import com.rndash.mbheadunit.CanFrame // AUTO GEN
import com.rndash.mbheadunit.nativeCan.CanBusNative // AUTO GEN

/**
 *   Generated by db_converter.py
 *   Object for SAM_V_A4 (ID 0x02CC)
**/

object SAM_V_A4 {

    	/** Gets outside mirror glass to the left **/
	fun get_sp_n_li() : Boolean = CanBusNative.getECUParameterB(CanBAddrs.SAM_V_A4, 7, 1) != 0
	
	/** Sets outside mirror glass to the left **/
	fun set_sp_n_li(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 7, 1, if(p) 1 else 0)
	
	/** Gets Outside mirror glass to the right **/
	fun get_sp_n_re() : Boolean = CanBusNative.getECUParameterB(CanBAddrs.SAM_V_A4, 6, 1) != 0
	
	/** Sets Outside mirror glass to the right **/
	fun set_sp_n_re(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 6, 1, if(p) 1 else 0)
	
	/** Gets outside mirror glass upwards **/
	fun get_sp_n_ob() : Boolean = CanBusNative.getECUParameterB(CanBAddrs.SAM_V_A4, 5, 1) != 0
	
	/** Sets outside mirror glass upwards **/
	fun set_sp_n_ob(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 5, 1, if(p) 1 else 0)
	
	/** Gets Outside mirror glass down **/
	fun get_sp_n_un() : Boolean = CanBusNative.getECUParameterB(CanBAddrs.SAM_V_A4, 4, 1) != 0
	
	/** Sets Outside mirror glass down **/
	fun set_sp_n_un(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 4, 1, if(p) 1 else 0)
	
	/** Gets Outside mirror after garage position **/
	fun get_sp_garage() : Boolean = CanBusNative.getECUParameterB(CanBAddrs.SAM_V_A4, 3, 1) != 0
	
	/** Sets Outside mirror after garage position **/
	fun set_sp_garage(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 3, 1, if(p) 1 else 0)
	
	/** Gets Outside mirror according to driving position **/
	fun get_sp_fahren() : Boolean = CanBusNative.getECUParameterB(CanBAddrs.SAM_V_A4, 2, 1) != 0
	
	/** Sets Outside mirror according to driving position **/
	fun set_sp_fahren(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 2, 1, if(p) 1 else 0)
	
	/** Gets Mirror adjustment switch setting **/
	fun get_spvs_st() : Boolean = CanBusNative.getECUParameterB(CanBAddrs.SAM_V_A4, 0, 1) != 0
	
	/** Sets Mirror adjustment switch setting **/
	fun set_spvs_st(f: CanFrame, p: Boolean) = CanBusNative.setFrameParameter(f, 0, 1, if(p) 1 else 0)
	
	
}