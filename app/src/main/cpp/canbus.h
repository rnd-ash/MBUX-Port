//
// Created by ashcon on 8/9/20.
//

#ifndef MERCEDES_UI_CANBUS_H
#define MERCEDES_UI_CANBUS_H

#include "jni.h"
#include <android/log.h>

#define LOG_TAG "Canbus-Native"

extern "C"
/**
 * Adds bytes coming from the Arduino to the canbus byte buffer
 * @param pEnv
 * @param bytes
 */
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_CanbusNative_addBytes(JNIEnv *env, jobject thiz, jbyteArray byte_array);

#endif //MERCEDES_UI_CANBUS_H
