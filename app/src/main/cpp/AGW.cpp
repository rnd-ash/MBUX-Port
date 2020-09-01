
#include "AGW.h"

FrameQueue sendFrames;

AGW::AGW() {
    this->audio_display = new AudioDisplay("AUDIO OFF", "AUX ");
    this->tel_display = new TelephoneDisplay("TEL ");
    this->thread_stop = false;
    this->worker = std::thread(&AGW::worker_loop, this);
    this->worker.detach();
}

void AGW::stopThread() {
    this->thread_stop = true;
}

void AGW::processKombiFrame(CanFrame *f) {
    switch (f->data[0] & 0xF0) {
        case 0x00:
            unpackKombiMsg(f);
            break;
        case 0x30:
            onFlowControl();
            break;
        default:
            __android_log_print(ANDROID_LOG_ERROR, "AGW", "Unknown Kombi frame starting with %02X", f->data[0]);
    }
}

void AGW::onFlowControl() {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW", "Flow control received");
    uint8_t frame_index = 0x21;
    CanFrame f = {'B', 0x01A4, 0x08};
    if (isSending) {
        while(tempBufferPos < tempBuffer.len) {
            memcpy(&f.data[1], &tempBuffer.data[tempBufferPos], std::min(7, tempBuffer.len - tempBufferPos));
            f.data[0] = frame_index;
            frame_index++;
            tempBufferPos += 7;
            sendFrames.pushFrame(f);
        }
        isSending = false;
    }
}

/**
 * Unpacks a payload message from the IC. The payload message can be 1 of 2 types:
 * * Response ACK Packet
 * * Data packet
 *
 * A response ACK Packet is only 3 bytes long, and has a last byte of either 0x06 (OK), or 0x15 (FAIL)
 *
 * A Data packet can be anywhere from 3 bytes to 7 bytes.
 * If 3 bytes, there is no args included, and the final byte after PKGID is a checksum, so can be ignored
 * If more than 3 bytes, the last byte is a checksum (All bytes must add to 254).
 *
 * @param f Can Frame from Kombi
 */
void AGW::unpackKombiMsg(CanFrame *f) {
    // Kombi ACK Package:
    // 0x03 0x05 0x20 0x06
    // 0x03 <PG> <PKG> <RES>
    if (f->data[0] == 0x03 && (f->data[3] == 0x06 || f->data[3] == 0x15)) {
        processKombiResponse(f->data[1], f->data[2], f->data[3]);
    } else {
        // Payload pkg
        // 0x05 0x05  0x20 0x02 0x11 0xC3
        //      <PG> <PKG> < ARGS >  <CS> (Last byte is always checksum)
        if (f->data[0] == 0x03) { // Payload without args (No checksum included)!
            processKombiPayload(f->data[1], f->data[2], 0, nullptr);
        } else {
            // More than 3 bytes, last byte is checksum, so ignore it by reducing argSize by 1
            processKombiPayload(f->data[1], f->data[2], f->data[0]-2, &f->data[3]);
        }
    }
}

void AGW::processKombiResponse(uint8_t page, uint8_t pkg, uint8_t result) {
    switch (page) {
        case PAGE_AUDIO:
            audio_display->processResponse(pkg, result);
            break;
        case PAGE_TEL:
            tel_display->processResponse(pkg, result);
            break;
        default:
            __android_log_print(
                    ANDROID_LOG_WARN,
                    "AGW_HANDLER",
                    "IC Response Invalid page! (DISP %02X, PKG %02X, RES %02X)",
                    page, pkg, result
                );
            break;
    }
}

void AGW::processKombiPayload(uint8_t page, uint8_t pkg, uint8_t len, uint8_t *data) {
    switch (page) {
        case PAGE_AUDIO:
            audio_display->processIncommingMessage(pkg, len, data);
            break;
        case PAGE_TEL:
            tel_display->processIncommingMessage(pkg, len, data);
            break;
        default:
            __android_log_print(
                    ANDROID_LOG_WARN,
                    "AGW_HANDLER",
                    "IC Payload Invalid page! (DISP %02X, PKG %02X)",
                    page, pkg
            );
            break;
    }
}

/**
 * Worker loop thread for AGW. This handles updating pages on a timer and sending them to Kombi
 */
void AGW::worker_loop() {
    bool updateTel = false;
    //this->tel_display->start_init();
    this->audio_display->start_init();
    while(!thread_stop) {
        if (!isSending) {
            //if (!updateTel) {
                audio_display->update();
                AGWPayload *p = audio_display->getOutgoing();
                if (p != nullptr) {
                    sendPayload(*p);
                    audio_display->popSendQueue();
                }
            //} else {
                /*
                tel_display->update();
                AGWPayload *p = tel_display->getOutgoing();
                if (p != nullptr) {
                    sendPayload(*p);
                    tel_display->popSendQueue();
                }
                 */
            //}
            //updateTel = !updateTel; // Alternate on ticks as to who to update
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(1));
    }
}


void AGW::sendPayload(AGWPayload p) {
    CanFrame f = { 'B', 0x01A4, 0x08, p.len };
    if (p.len <= 7) { // Can be done in 1 frame!
        isSending = true;
        memcpy(&f.data[1], &p.data[0], p.len);
        sendFrames.pushFrame(f);
        isSending = false;
    } else {
        isSending = true;
        f.data[0] = 0x10;
        f.data[1] = p.len;
        tempBuffer = p; // Copy
        tempBufferPos = 6;
        memcpy(&f.data[2], &p.data[0], 6);
        sendFrames.pushFrame(f);
    }
}