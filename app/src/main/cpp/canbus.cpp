#include "canbus.h"
#include <unistd.h>

unsigned long bytes_from_bus = 0;
const int extra_bytes = 6; // Bytes added to canframe (Header + CRC)
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
            res -= 1; // This removes the newline chars from the end (\n)
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
            bytes_from_bus += read.dlc + extra_bytes;
        } else {
            usleep(100); // Don't destroy the CPU when there is no data!
        }
    }
    __android_log_print(ANDROID_LOG_DEBUG, "ParseThread", "Quitting parser thread");
}

inline uint8_t strToInt(char x) {
    return (x >= 'A') ? (x - 'A' + 10) : (x - '0');
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_KombiDisplay_setAudioBody(JNIEnv *env, jobject thiz, jbyte fmt, jstring text) {
    const char* c = env->GetStringUTFChars(text, nullptr);
    decoder->agw->audio_display->setBody(std::string(c), fmt);
    env->ReleaseStringUTFChars(text, c);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_KombiDisplay_setAudioSymbolBytes(JNIEnv *env, jobject thiz, jbyte u, jbyte d) {
    decoder->agw->audio_display->setSymbols(u, d);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_rndash_mbheadunit_CarComm_00024Companion_getRxRate(JNIEnv *env, jobject thiz) {
    unsigned long tmp = bytes_from_bus;
    bytes_from_bus = 0;
    return tmp;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_KombiDisplay_setTelSymbolBytes(JNIEnv *env, jobject thiz,
                                                                    jbyte s1, jbyte s2, jbyte s3,
                                                                    jbyte s4) {
    decoder->agw->tel_display->setSymbols(s1, s2, s3, s4);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_nativeCan_KombiDisplay_setTelBody(JNIEnv *env, jobject thiz, jbyte fmt1,
                                                             jstring text1, jbyte fmt2,
                                                             jstring text2, jbyte fmt3,
                                                             jstring text3, jbyte fmt4,
                                                             jstring text4) {
    const char* c1 = env->GetStringUTFChars(text1, nullptr);
    const char* c2 = env->GetStringUTFChars(text2, nullptr);
    const char* c3 = env->GetStringUTFChars(text3, nullptr);
    const char* c4 = env->GetStringUTFChars(text4, nullptr);
    decoder->agw->tel_display->setBody(
            std::string(c1), fmt1,
            std::string(c2), fmt2,
            std::string(c3), fmt3,
            std::string(c4), fmt4
        );
    env->ReleaseStringUTFChars(text1, c1);
    env->ReleaseStringUTFChars(text2, c2);
    env->ReleaseStringUTFChars(text3, c3);
    env->ReleaseStringUTFChars(text4, c4);
}