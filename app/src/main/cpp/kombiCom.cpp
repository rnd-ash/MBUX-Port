//
// Created by ashcon on 8/27/20.
//

#include "kombiCom.h"
#include <mutex>

FrameQueue sendFrames;

void kombiCom::processKombiMsg(CanFrame *f) {
    if (f->dlc != 8) { // Check for payload integrity
        __android_log_print(ANDROID_LOG_WARN, "AGW", "Response DLC Not valid");
        return;
    }

    switch(f->data[0] & 0xF0) { // Extract first nibble of ISO15765
        case 0x00:
            processKombiMessage(f->data[0], &f->data[1]);
            break;
        case 0x30:
            // Dont give a crap about flow control, latency between this a JVM is long enough
            break;
        default:
            __android_log_print(ANDROID_LOG_WARN, "AGW", "Unknown packet byte %02X", f->data[0]);
            break;
    }
}

void kombiCom::processKombiMessage(int len, uint8_t *data) {
    processPackage(data[0], data[1], len-2, &data[2]);
}


void kombiCom::processFC(CanFrame *f) {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW", "FC OK");
    continuePayloadSend();
}

void kombiCom::processPackage(uint8_t display, uint8_t pkgID, uint8_t argsSize, uint8_t *args) {
    if (pkgID == 0x04) {
        // Magic wakeup
        __android_log_print(ANDROID_LOG_DEBUG, "AGW", "WAKEUP RECEIVED - %02X", display);
        respondOK(display, pkgID);
        sendPackage20(DISPLAY_TEL); // Tell KOMBI we can do Telephone
        sendPackage20(DISPLAY_AUDIO); // Tell KOMBI we can do AUDIO
        return;
    }
    if (argsSize == 1) { // Response package from KOMBI
        processKombiResponse(display, pkgID, args[0]);
    } else {
        // Kombi is sending us some data to interpret - Log it and tell Kombi we got the data OK!
        char buf[20]{0x00};
        int pos = 0;
        for (int i = 0; i < argsSize; i++) {
            pos += sprintf(buf+pos, "%02X ", args[i]);
        }
        __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Display %02X, Package %02X, ARGS: %s", display, pkgID, buf);
        respondOK(display, pkgID);
    }
}

void kombiCom::processKombiResponse(uint8_t display, uint8_t pkgID, uint8_t resp) {
    if (resp == 0x06) {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Kombi received package %02X OK (Display = %02X)!", pkgID, display);
        if (pkgID == 0x24) {
            if (display == 0x05) init_header_tel = true;
            else if (display == 0x03) init_header_audio = true;
        } else if (pkgID == 0x26) {
            if (display == 0x05) init_body_tel = true;
            else if (display == 0x03) init_body_audio = true;
        }
    } else if (resp == 0x15) {
        __android_log_print(ANDROID_LOG_WARN, "AGW", "Kombi failed to receive package %02X! (Display = %02X)!", pkgID, display);
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Unknown response for package %02X (Display = %02X) (%02X)!", pkgID, display, resp);
    }
}

// Tell kombi we got the package!
void kombiCom::respondOK(uint8_t display, uint8_t pkgID) {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Responding OK to package %02X",pkgID);
    uint8_t args[3] = {display, pkgID, 0x06};
    sendIsoBuffer(3, args);
    if (pkgID == 0x21) {
        sendPackage24(display);
    } else if (pkgID == 0x25) {
        sendPackage26(display);
    }
}

void kombiCom::sendPackage20(uint8_t page) {
    if (page == DISPLAY_AUDIO) {
        uint8_t args[5] = {DISPLAY_AUDIO, 0x20, 0x02, 0x11, 0xC3};
        sendIsoBuffer(5, args);
    } else if (page == DISPLAY_TEL) {
        uint8_t args[5] = {DISPLAY_TEL, 0x20,0x02,  0x11, 0xC1};
        sendIsoBuffer(5, args);
    } else {
        __android_log_print(ANDROID_LOG_WARN, "AGW", "Invalid page for package 20 - %02X", page);
    }
}

// Gets called on flow control receive - Sends the remaining frames to KOMBI
void kombiCom::continuePayloadSend() {
    if (!isoSendComplete) {
        CanFrame f = {'B', 0x01A4, 0x08};
        uint8_t start = 0x21;
        // Send the rest of the ISO Data built from sendIsoBuffer
        while(iso_buffer_pos < iso_buffer_size) {
            f.data[0] = start;
            memcpy(&f.data[1], &iso_buffer[iso_buffer_pos], std::min(7, iso_buffer_size-iso_buffer_pos));
            sendFrames.pushFrame(f);
            start++;
            iso_buffer_pos+=7;
        }
        delete[] iso_buffer; // Can de-allocate now
        isoSendComplete = true;
    }
}

void kombiCom::sendIsoBuffer(uint8_t argsSize, uint8_t* args) {
    CanFrame f = {'B', 0x01A4, 0x08};
    if (argsSize <= 7) { // Can be done in 1 frame!
        f.data[0] = argsSize;
        memcpy(&f.data[1], &args[0], argsSize);
        sendFrames.pushFrame(f);
        isoSendComplete = true;
    } else if (isoSendComplete) { // Check if buffer is in use (True implies sending is done so we can use it)
        isoSendComplete = false;
        // Allocate a buffer in memory to store everything
        f.data[0] = 0x10;
        f.data[1] = argsSize;
        memcpy(&f.data[2], &args[0], 6);
        iso_buffer = new uint8_t [argsSize-6];
        memcpy(&iso_buffer[0], &args[6], argsSize-6);
        iso_buffer_size = argsSize-6;
        iso_buffer_pos = 0;
        sendFrames.pushFrame(f);
        continuePayloadSend();
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "AGW", "ISO BUFFER IN USE!");
    }
}

void kombiCom::sendPackage24(uint8_t page) {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Sending package 24 for page %02X", page);
    // Placeholder - Just do what works
    if (page == DISPLAY_AUDIO) {
        isoBuffer x = audioPage.generatePackage24();
        sendIsoBuffer(x.len, x.ptr);

    } else if (page == DISPLAY_TEL) {
        isoBuffer x = telPage.generatePackage24();
        uint8_t buf[28] = {0x05, 0x24, 0x02, 0x60, 0x01, 0x04, 0x00, 0x00, 0x00, 0x15, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05, 0x00, 0x54, 0x45, 0x4C, 0x20, 0x20, 0x00, 0xC7};
        sendIsoBuffer(28, buf);
        //sendIsoBuffer(x.len, x.ptr);
    }
}

void kombiCom::sendPackage26(uint8_t page) {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Sending package 26 for page %02X", page);
    if (page == DISPLAY_AUDIO) {
        isoBuffer x = audioPage.generatePackage26();
        sendIsoBuffer(x.len, x.ptr);
    } else if (page == DISPLAY_TEL) {
        uint8_t buf[22] = {0x05, 0x26, 0x01, 0x00, 0x04, 0x04, 0x10, 0x4E, 0x4F, 0x07, 0x10, 0x50, 0x48, 0x4F, 0x4E, 0x45, 0x02, 0x10, 0x02, 0x10, 0x00, 0x97};
        sendIsoBuffer(22, buf);
        //isoBuffer x = telPage.generatePackage26();
        //sendIsoBuffer(x.len, x.ptr);
    }
}

void kombiCom::sendPackage29(uint8_t page) {
    if (page == DISPLAY_AUDIO) {
        isoBuffer x = audioPage.generatePackage29();
        sendIsoBuffer(x.len, x.ptr);
    } else if (page == DISPLAY_TEL) {

    }
}

bool kombiCom::init_complete() const {
    return init_body_audio && init_body_tel && init_header_audio && init_header_tel;
}

void kombiCom::thread_loop() {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Comm thread started!");
    std::this_thread::sleep_for(std::chrono::milliseconds(3000));
    if (!init_complete()) {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Not finished init - will send remaining packets");
        if (!this->init_header_tel) {
            this->sendPackage24(DISPLAY_TEL);
            std::this_thread::sleep_for(std::chrono::milliseconds(250));
        }
        if (!this->init_header_audio) {
            this->sendPackage24(DISPLAY_AUDIO);
            std::this_thread::sleep_for(std::chrono::milliseconds(250));
        }
        if (!this->init_body_tel) {
            this->sendPackage26(DISPLAY_TEL);
            std::this_thread::sleep_for(std::chrono::milliseconds(250));
        }
        if (!this->init_body_audio) {
            this->sendPackage26(DISPLAY_AUDIO);
            std::this_thread::sleep_for(std::chrono::milliseconds(250));
        }
    }
    while(!thread_stop) {
        if (this->curr_page == DISPLAY_AUDIO) {
            this->audioPage.update();
            if (this->audioPage.update_body) {
                this->audioPage.update_body = false;
                this->sendPackage26(DISPLAY_AUDIO);
            }
            if (this->audioPage.update_symbols) {
                this->audioPage.update_symbols = false;
                this->audioPage.update_header = false; // Since 24 sends header as well
                this->sendPackage24(DISPLAY_AUDIO);
            }
            if (this->audioPage.update_header) {
                this->audioPage.update_header = false;
                this->sendPackage29(DISPLAY_AUDIO);
            }
        } else if (this->curr_page == DISPLAY_TEL) {
            // TODO Telephone page stuff
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(10));
    }
    __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Comm thread terminated!");
}

kombiCom::kombiCom() {
    this->thread_stop = false;
    this->kombi_thread = std::thread(&kombiCom::thread_loop, this);
    this->kombi_thread.detach();
}

void kombiCom::stopThread() {
    this->thread_stop = true;
}

void kombiCom::setCurrentPage(uint8_t pg) {
    this->curr_page = pg;
}
