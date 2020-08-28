#ifndef MERCEDES_UI_CANBUS_H
#define MERCEDES_UI_CANBUS_H

#include <android/log.h>
#include <jni.h>
#include "readBuffer.h"
#include <thread>
#include "CanbusDecoder.h"

#define LOG_TAG "CanbusNative"

// Canbus data

uint8_t strToInt(char x);
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

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_getNativeFrame(JNIEnv *env, jobject thiz,
                                                                 jint ecu_addr, jchar bus_id) {
    try {
        CanFrame *f = decoder->getFrame(bus_id, ecu_addr);
        if (f == nullptr) {
            env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Null ECU Frame!");
        } else {
            uint8_t* temp = new uint8_t [f->dlc+2];
            temp[0] = f->id >> 8;
            temp[1] = f->id;
            memcpy(&temp[2], &f->data[0], f->dlc);

            jbyteArray bytes = env->NewByteArray(2 + f->dlc);
            env->SetByteArrayRegion(bytes, 0, 2+f->dlc, (jbyte*)(temp));
            delete[] temp;
            return bytes;
        }
    } catch (InvalidECUAddressException e) {
        env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), e.what().c_str());
    } catch (InvalidBusException e) {
        env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), e.what().c_str());
    }
    return NULL;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_KombiDisplay_setPage(JNIEnv *env, jobject thiz, jbyte pg) {
    decoder->kombi->setCurrentPage((uint8_t)pg);
}




#endif
extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_KombiDisplay_setHeaderAttrs(JNIEnv *env, jobject thiz,jbyte page, jbyte fmt,jstring text) {
    const char* c = env->GetStringUTFChars(text, nullptr);
    if (page == 0x03) {
        decoder->kombi->audioPage.setHeader(std::string(c), (uint8_t)fmt);
    } else if (page == 0x05) {

    }
    env->ReleaseStringUTFChars(text, c);
}