#include "canbus.h"
#include <unistd.h>

char* charreadbuf = new char[50]; // Max size of can frame encoded from Arduino
extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_init(JNIEnv* env, jobject thiz) {
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Native canbus init!");
    // Create and startup the parser thread!
    parserThread = std::thread(processFrames);
    parserThread.detach();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_destroy(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Native canbus shutdown!");
    thread_cancel = true; // Tells parser thread to quit
    decoder->agw->stopThread();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_sendBytesToBuffer(JNIEnv *env, jobject thiz, jbyteArray bytes, jint numBytes) {
    if (numBytes == 0){
        return;
    }
    auto temp = env->GetByteArrayElements(bytes, nullptr);
    readbuff->insertElements((uint8_t*)temp, numBytes);
    env->ReleaseByteArrayElements(bytes, temp, JNI_ABORT);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_rndash_mbheadunit_nativeCan_CanBusNative_getSendFrame(JNIEnv *env, jobject thiz) {
    if (sendFrames.hasFrame()) { // Queue has a frame to send
        CanFrame front = sendFrames.getFront();
        jbyteArray ret = env->NewByteArray(12); // Allocate a new jbyteArray
        // Copy array content from first in queue
        env->SetByteArrayRegion(ret, 0, 1, (jbyte*)&front.busID);
        env->SetByteArrayRegion(ret, 1, 2, (jbyte*)&front.id);
        env->SetByteArrayRegion(ret, 3, 1, (jbyte*)&front.dlc);
        env->SetByteArrayRegion(ret, 4, 8, (jbyte*)&front.data);
        return ret; // Return the populated array
    } else {
        return NULL; // No data, return empty jbytearray
    }
}

void processFrames() {
    __android_log_print(ANDROID_LOG_DEBUG, "ParseThread", "Starting thread");
    CanFrame read = {0x00};
    while(!thread_cancel) {
        // Now check if we can read a canframe
        int res = readbuff->readUntil((uint8_t*)&charreadbuf[0], 50, 0x0A);
        if (res != 0) {
            res -= 2; // This removes the 2 newline chars from the end (\r\n)
            if (res < 7 || res > 21 || res % 2 == 0) { // Error checking for bad frames - Should always be odd
                __android_log_print(ANDROID_LOG_WARN, LOG_TAG, "Discarding bad frame");
                continue;
            }
            // Now process the CAN Frame input
            read.busID = charreadbuf[0];
            read.dlc = (res - 5)/2;
            read.id = strToInt(charreadbuf[1]) << 12 |
                    strToInt(charreadbuf[2]) << 8 |
                    strToInt(charreadbuf[3]) << 4 |
                    strToInt(charreadbuf[4]);

            int nibpos = 5;
            for (int i = 0; i < read.dlc; i++) {
                read.data[i] = strToInt(charreadbuf[nibpos]) << 4 | strToInt(charreadbuf[nibpos+1]);
                nibpos+=2;
            }
            decoder->processFrame(&read);
        } else {
            usleep(1000); // Don't destroy the CPU when there is no data!
        }
    }
    __android_log_print(ANDROID_LOG_DEBUG, "ParseThread", "Quitting parser thread");
}

uint8_t strToInt(char x) {
    return (x >= 'A') ? (x - 'A' + 10) : (x - '0');
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_KombiDisplay_setBodyAttrs(JNIEnv *env, jobject thiz, jbyte page, jbyte fmt, jstring text) {
    const char* c = env->GetStringUTFChars(text, nullptr);
    if (page == 0x03) {
        decoder->agw->audio_display->body =std::string(c);
        decoder->agw->audio_display->body_fmt = (uint8_t)fmt;
    } else if (page == 0x05) {

    }
    env->ReleaseStringUTFChars(text, c);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_KombiDisplay_setAudioSymbolBytes(JNIEnv *env, jobject thiz, jbyte u, jbyte d) {
    decoder->agw->audio_display->symbolLower = d;
    decoder->agw->audio_display->symbolUpper = d;
}