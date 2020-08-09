#include "canbus.h"
#include "stdlib.h"
extern "C" JNIEXPORT void JNICALL
Java_com_rndash_mbheadunit_CanbusNative_addBytes(JNIEnv *env, jobject thiz, jbyteArray byte_array) {
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Got %d bytes", env->GetArrayLength(byte_array));
}