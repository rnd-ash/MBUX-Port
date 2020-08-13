#include "canbus.h"
char* charreadbuf = new char[50]; // Max size of can frame encoded from Arduino
extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_CanBusNative_init(JNIEnv* env, jobject thiz) {
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Native canbus init!");
    // Create and startup the parser thread!
    parserThread = std::thread(processFrames);
    parserThread.detach();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_CanBusNative_destroy(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Native canbus shutdown!");
    thread_cancel = true; // Tells parser thread to quit
}

extern "C"
JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_CanBusNative_sendBytesToBuffer(JNIEnv *env, jobject thiz, jbyteArray bytes, jint numBytes) {
    if (numBytes == 0){
        return;
    }
    auto temp = env->GetByteArrayElements(bytes, nullptr);
    readbuff->insertElements((uint8_t*)temp, numBytes);
    env->ReleaseByteArrayElements(bytes, temp, JNI_ABORT);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_rndash_mbheadunit_CanBusNative_getSendFrame(JNIEnv *env, jobject thiz) {
    if (!sendQueue.empty()) { // Queue has a frame to send
        jbyteArray ret = env->NewByteArray(sizeof(CanFrame)); // Allocate a new jbyteArray
        // Copy array content from first in queue
        env->SetByteArrayRegion(ret, 0, sizeof(CanFrame), (jbyte*)&sendQueue.front());
        sendQueue.pop(); // Pop the queue
        return ret; // Return the populated array
    } else {
        return env->NewByteArray(0); // No data, return empty jbytearray
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
            int pos = 0;
            char buf[25] = {0x00};
            for (int i = 0; i < read.dlc; i++) {
                pos += sprintf(buf+pos, "%02X ", read.data[i]);
            }
            __android_log_print(ANDROID_LOG_DEBUG, "ParseThread", "CAN FRAME: Bus: %c, ID: %04X, DLC: %d. Data: [%s]", read.busID, read.id, read.dlc, buf);
        }
    }
    __android_log_print(ANDROID_LOG_DEBUG, "ParseThread", "Quitting parser thread");
}

uint8_t strToInt(char x) {
    return (x >= 'A')? (x - 'A' + 10): (x - '0');
}