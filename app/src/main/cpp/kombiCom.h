//
// Created by ashcon on 8/27/20.
//

#ifndef MERCEDES_UI_KOMBICOM_H
#define MERCEDES_UI_KOMBICOM_H

#include "ECUs/ECU.h"
#include "KombiDisplay.h"
#include <thread>

extern FrameQueue sendFrames;


class kombiCom {
public:
    kombiCom();
    void processKombiMsg(CanFrame *f);
    void stopThread();
    void setCurrentPage(uint8_t pg);
    TelPage telPage = TelPage();
    AudioPage audioPage = AudioPage();
private:
    uint8_t curr_page;
    bool thread_stop = false;
    void thread_loop();
    void processKombiMessage(int len, uint8_t* data);
    void processPackage(uint8_t display, uint8_t pkgID, uint8_t  argsSize, uint8_t* args);
    void processKombiResponse(uint8_t display, uint8_t pkgID, uint8_t resp);
    void processFC(CanFrame *f);
    void respondOK(uint8_t display, uint8_t pkgID);
    void sendPackage20(uint8_t page);
    void sendPackage24(uint8_t page); // Configure page with header and symbols
    void sendPackage26(uint8_t page); // Set body text
    void sendPackage29(uint8_t page); // Set header text
    uint8_t continue_packet = 0x00;
    void continuePayloadSend();
    uint8_t iso_buffer_size = 0;
    uint8_t iso_buffer_pos = 0;
    uint8_t* iso_buffer; // For sending multiple payloads
    void sendIsoBuffer(uint8_t argsSize, uint8_t* args);
    bool init_complete() const;
    bool isoSendComplete = true;
    bool init_header_tel = false;
    bool init_header_audio = false;
    bool init_body_tel = false;
    bool init_body_audio = false;
    std::thread kombi_thread;
};


#endif //MERCEDES_UI_KOMBICOM_H
