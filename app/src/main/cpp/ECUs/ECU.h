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

typedef union {
    uint64_t uint64;
    uint8_t bytes[8];
} BytesUnion;

typedef struct {
    char busID;
    uint16_t id;
    uint8_t dlc;
    BytesUnion data;
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
        uint64_t data_tmp = data.data.uint64;
        char* x = (char*)(&data_tmp);
        char* y = (char*) x + sizeof(data_tmp);
        std::reverse(x, y);

        uint64_t mask = 0;
        for (int i = 0; i < len; i++) {
            mask |= (1 << i);
        }
        return (data_tmp >> (64 -(offset+len))) & mask;
    }

    bool setParam(int value, int offset, int len) {
        return false;
    }
protected:
    CanFrame data = {0x00};
};

#endif //MERCEDES_UI_ECU_H
