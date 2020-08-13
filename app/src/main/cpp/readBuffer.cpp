//
// Created by ashcon on 8/10/20.
//

#include "readBuffer.h"
#include <android/log.h>
#include <cstdlib>
#include <cstring>
#include <algorithm>

readBuffer::readBuffer(int maxCapacity) {
    __android_log_print(ANDROID_LOG_DEBUG, RLOG_TAG, "Creating read buffer for %d bytes", maxCapacity);
    this->maxElements = maxCapacity;
    this->storedElements = 0;
    this->buffer = (uint8_t*)malloc(maxCapacity);
    memset(this->buffer, 0x00, maxCapacity);
}

readBuffer::~readBuffer() {
    __android_log_print(ANDROID_LOG_DEBUG, RLOG_TAG, "Destructor called!");
    free(this->buffer);
}

void readBuffer::insertElement(uint8_t elm) {
    mutex.lock();
    if (storedElements == maxElements) {
        __android_log_print(ANDROID_LOG_ERROR, RLOG_TAG, "Buffer full!");
        mutex.unlock();
        return;
    }
    this->buffer[storedElements] = elm;
    this->storedElements++;
    mutex.unlock();
}

void readBuffer::insertElements(uint8_t *ptr, int numElements) {
    mutex.lock();
    if (storedElements+numElements >= maxElements) {
        __android_log_print(ANDROID_LOG_ERROR, RLOG_TAG, "Buffer full, cannot add %d elements!", numElements);
        mutex.unlock();
        return;
    }
    /*
    char buf[400] = {0x00};
    int pos = 0;
    for (int i = 0; i < std::min(50,numElements); i++) {
        pos += sprintf(buf+pos, "%02X ", ptr[i]);
    }
    __android_log_print(ANDROID_LOG_DEBUG, RLOG_TAG, "IN : %s", buf);
     */
    memcpy(&buffer[storedElements], &ptr[0], numElements);
    storedElements += numElements;
    //__android_log_print(ANDROID_LOG_DEBUG, RLOG_TAG, "Buffer has %d elements", storedElements);
    mutex.unlock();
}

int readBuffer::getSize() const {
    return storedElements;
}

int readBuffer::readUntil(uint8_t *ptr, int maxBytes, uint8_t until) {
    int pos = 0;
    mutex.lock();
    while(true) {
        if ((pos == maxBytes) || (pos == storedElements)) { // Still haven't found it yet
            mutex.unlock();
            return 0; // Cannot find the byte required in time
        }
        if (buffer[pos] == until) {
            memcpy(&ptr[0], &buffer[0], pos);
            shiftBuffer(pos+1); // +1 to read it!
            mutex.unlock();
            return pos+1;
        }
        pos++;
    }
}

void readBuffer::shiftBuffer(int amount) {
    if (storedElements - amount != 0) { // Only do shifting if array is not 'empty'
        memmove(&buffer[0], &buffer[amount], storedElements-amount);
    }
    storedElements -= amount;
}

