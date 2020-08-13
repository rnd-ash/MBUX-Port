#ifndef MERCEDES_UI_CANBUS_H
#define MERCEDES_UI_CANBUS_H

#include <queue>
#include <android/log.h>
#include <jni.h>
#include "readBuffer.h"
#include <thread>
#define LOG_TAG "CanbusNative"

// Canbus data
#define CANC 'C'
#define CANB 'B'

typedef struct {
    char busID;
    uint16_t id;
    uint8_t dlc;
    uint8_t data[8] __attribute__((aligned(8)));
} CanFrame;

uint8_t strToInt(char x);
std::queue<CanFrame> sendQueue; // Queue of frames to send to Arduino
readBuffer* readbuff = new readBuffer(8192); // 8KB Read buffer

bool thread_cancel = false;
void processFrames(); // Loop for thread
std::thread parserThread; // Thread that constantly reads the buffer and parses built can frames



extern "C"
/**
 * Initializes the native canbus environment
 */
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_CanBusNative_init(JNIEnv* env, jobject thiz);


extern "C"
/**
 * Destroys the native canbus environment
 */
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_CanBusNative_destroy(JNIEnv *env, jobject thiz);

extern "C"
/**
 * Returns a byte array representing a can frame that is to be sent to the arduino.
 * If the result is empty, then no frame is in the queue to send
 */
JNIEXPORT jbyteArray JNICALL
Java_com_rndash_mbheadunit_CanBusNative_getSendFrame(JNIEnv *env, jobject thiz);

extern "C"
/**
 *
 */
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_CanBusNative_sendBytesToBuffer(JNIEnv *env, jobject thiz, jbyteArray bytes, jint numBytes);
#endif