
@file:Suppress("unused", "FunctionName")
package com.rndash.mbheadunit.nativeCan.canC
import com.rndash.mbheadunit.CanFrame // AUTO GEN
import com.rndash.mbheadunit.nativeCan.CanBusNative // AUTO GEN

/**
 *   Generated by db_converter.py
 *   Object for FS_340h (ID 0x0340)
**/

object FS_340h {

    	/** Gets Load torque ABC pump **/
	fun get_m_last() : Int = CanBusNative.getECUParameterC(CanCAddrs.FS_340h, 60, 4)
	
	/** Sets Load torque ABC pump **/
	fun set_m_last(f: CanFrame, p: Int) = CanBusNative.setFrameParameter(f, 60, 4, p)
	
	
}