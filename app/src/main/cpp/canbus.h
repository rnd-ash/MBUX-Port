#ifndef MERCEDES_UI_CANBUS_H
#define MERCEDES_UI_CANBUS_H

#include <queue>
#include <android/log.h>
#include <jni.h>
#include "readBuffer.h"
#include <thread>
#include "ECUs/ECU.h"
#include "CanbusDecoder.h"

#define LOG_TAG "CanbusNative"

// Canbus data

uint8_t strToInt(char x);
std::queue<CanFrame> sendQueue; // Queue of frames to send to Arduino
readBuffer* readbuff = new readBuffer(8192); // 8KB Read buffer

bool thread_cancel = false;
void processFrames(); // Loop for thread
std::thread parserThread; // Thread that constantly reads the buffer and parses built can frames

// Canbus Decoder
CanbusDecoder* decoder = new CanbusDecoder();


extern "C"
/**
 * Initializes the native canbus environment
 */
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_init(JNIEnv* env, jobject thiz);


extern "C"
/**
 * Destroys the native canbus environment
 */
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_destroy(JNIEnv *env, jobject thiz);

extern "C"
/**
 * Returns a byte array representing a can frame that is to be sent to the arduino.
 * If the result is empty, then no frame is in the queue to send
 */
JNIEXPORT jbyteArray JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_getSendFrame(JNIEnv *env, jobject thiz);

extern "C"
/**
 *konko
 */
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_sendBytesToBuffer(JNIEnv *env, jobject thiz, jbyteArray bytes, jint numBytes);
#endif



// MESS BELOW
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rndash_mbheadunit_NativeCan_CanB_KLA_1A1_isHeatedRearWindows(JNIEnv *env, jobject thiz) {
    return false;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_getECUParam(JNIEnv *env, jobject thiz,
                                                              jint ecu_addr, jchar bus_id,
                                                              jint offset, jint len) {
    try {
        return decoder->getValue(bus_id, ecu_addr, offset, len);
    } catch (InvalidECUAddressException e) {
        env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), e.what().c_str());
    } catch (InvalidBusException e) {
        env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), e.what().c_str());
    } catch (InvalidSizeException e) {
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), e.what().c_str());
    }
    return 0;
}