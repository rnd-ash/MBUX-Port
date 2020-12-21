//
// Created by ashcon on 8/13/20.
//

#ifndef MERCEDES_UI_ECU_H
#define MERCEDES_UI_ECU_H

#include <cstdlib>
#include <cstring>
#include <string>
#include <android/log.h>
#include <mutex>
#include <queue>

#define CANC 'C'
#define CANB 'B'


typedef struct {
    char busID;
    uint16_t id;
    uint8_t dlc;
    uint8_t data[8] __attribute__((aligned(8)));
} CanFrame;

class FrameQueue {
public:
    void pushFrame(CanFrame f) {
        queueMutex.lock();
        sendQueue.push(f);
        queueMutex.unlock();
    }
    const inline bool hasFrame() {
        return !sendQueue.empty();
    }
    CanFrame getFront() {
        CanFrame f;
        queueMutex.lock();
        f = sendQueue.front();
        sendQueue.pop();
        queueMutex.unlock();
        return f;
    }
private:
    std::queue<CanFrame> sendQueue; // Queue of frames to send to Arduino
    std::mutex queueMutex;
};

class InvalidSizeException : public std::exception  {
public:
    InvalidSizeException(int m, int r) {
        this->max = m;
        this->requested = r;
    }
    std::string what() {
        char buf[100];
        sprintf(buf, "Bit pos %d is outside frame with max size %d", this->requested, this->max);
        return std::string(buf);
    }
private:
    int max;
    int requested;
};

class ECUFrame {
public:
    void setData(CanFrame* f) { memcpy(&this->data, f, sizeof(CanFrame)); };
    virtual CanFrame* getData() { return &this->data; };

    int getParam(int offset, int len) {
        if (offset + len > data.dlc*8) {
            throw InvalidSizeException(this->data.dlc*8, offset+len-1);
        }
        // Shortcut for booleans - just check 1 bit
        if (len == 1) {
            return (this->data.data[offset / 8] >> (7 - (offset % 8))) & 1;
        } else {
            int start = offset / 8;
            int end = (offset + len - 1) / 8; //-1 here as we start inclusive of start
            uint32_t d = this->data.data[start];
            if (start != end) {
                for (int i = start; i <= end; i++) {
                    d = (d | data.data[i]) << 8;
                }
            }
            uint32_t mask = 0x00;
            for (int i = 0; i < len; i++) {
                mask |= (1 << i);
            }
            // Now bit shift so that masking values start at the start of the byte
            return (d >> (offset % 8)) & mask;
        }
    }

    bool setParam(int value, int offset, int len) {
        return false;
    }
protected:
    CanFrame data = {0x00};
};

#endif //MERCEDES_UI_ECU_H
