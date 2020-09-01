//
// Created by ashcon on 8/27/20.
//

#ifndef MERCEDES_UI_AGW_H
#define MERCEDES_UI_AGW_H

#include "ECUs/ECU.h"
#include <thread>
#include <map>
#include <queue>
#include "AGWDisplay.h"

extern FrameQueue sendFrames;

enum AGW_ACTION {
    IDLE, // AGW is idle, doing nothing
    INIT_PAGE, // AGW is running the full init sequence (20, 24, 26)
    SEND_PKG20, // AGW is sending package 20
    SEND_PKG24, // AGW is sending package 24
    SEND_PKG26, // AGW is sending package 26
    SEND_PKG29, // AGW is sending package 29
};

enum AGW_WAIT_REASON {
    NONE, // Not waiting
    FLOW_CONTROL, // Waiting for flow control
    RESPONSE // Waiting for Kombi response for a package
};

class AGW {
public:
    AGW();
    void processKombiFrame(CanFrame *f);
    void stopThread();
    TelephoneDisplay* tel_display;
    AudioDisplay* audio_display;
private:
    void sendPayload(AGWPayload p);
    void unpackKombiMsg(CanFrame *f);
    void processKombiResponse(uint8_t page, uint8_t pkg, uint8_t result);
    void processKombiPayload(uint8_t page, uint8_t pkg, uint8_t len, uint8_t* data);
    void onFlowControl();
    void worker_loop();
    bool thread_stop;
    std::thread worker;
    AGWPayload tempBuffer;
    uint8_t tempBufferPos;
    bool isSending = false;
};


#endif //MERCEDES_UI_AGW_H
