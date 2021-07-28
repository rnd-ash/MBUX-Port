
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
    switch (f->data.bytes[0] & 0xF0) {
        case 0x00:
            unpackKombiMsg(f);
            break;
        case 0x30:
            if (f->data.bytes[0] == 0x30) {
                this->hasFC = true;
                this->bs = f->data.bytes[1];
                this->st_min = f->data.bytes[2];
                this->lastSendMillis = get_millis();
            }
            break;
        default:
            __android_log_print(ANDROID_LOG_ERROR, "AGW", "Unknown Kombi frame starting with %02X", f->data.bytes[0]);
            break;
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
    if (f->data.bytes[0] == 0x03 && (f->data.bytes[3] == 0x06 || f->data.bytes[3] == 0x15)) {
        processKombiResponse(f->data.bytes[1], f->data.bytes[2], f->data.bytes[3]);
    } else {
        // Payload pkg
        // 0x05 0x05  0x20 0x02 0x11 0xC3
        //      <PG> <PKG> < ARGS >  <CS> (Last byte is always checksum)
        if (f->data.bytes[0] == 0x03) { // Payload without args (No checksum included)!
            processKombiPayload(f->data.bytes[1], f->data.bytes[2], 0, nullptr);
        } else {
            // More than 3 bytes, last byte is checksum, so ignore it by reducing argSize by 1
            processKombiPayload(f->data.bytes[1], f->data.bytes[2], f->data.bytes[0]-2, &f->data.bytes[3]);
        }
    }
}

void AGW::processKombiResponse(uint8_t page, uint8_t pkg, uint8_t result) {
    switch (page) {
        case PAGE_AUDIO:
            audio_display->processResponse(pkg, result);
            break;
        case PAGE_NAVI:
            __android_log_print(ANDROID_LOG_WARN, "AGW_HANDLER", "NAVI Page is not implemented yet");
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
        case PAGE_NAVI:
            __android_log_print(ANDROID_LOG_WARN, "AGW_HANDLER", "NAVI Page is not implemented yet");
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
    this->tel_display->start_init(false);
    this->audio_display->start_init(false);
    uint8_t update_screen = PAGE_TEL;
    while(!thread_stop) {
        if (isSending && get_millis() - lastSendMillis > 2000) {
            __android_log_print(ANDROID_LOG_WARN,"AGW", "TIMEOUT SENDING, RESETTING BUFFER");
            isSending = false;
        }
        if (!isSending) {
            if (update_screen == PAGE_AUDIO) {
                audio_display->update();
                AGWPayload *p = audio_display->getOutgoing();
                if (p != nullptr) {
                    sendPayload(*p);
                    audio_display->popSendQueue();
                }
                update_screen = PAGE_TEL;
            } else if (update_screen == PAGE_TEL) {
                tel_display->update();
                AGWPayload *p = tel_display->getOutgoing();
                if (p != nullptr) {
                    sendPayload(*p);
                    tel_display->popSendQueue();
                }
                update_screen = PAGE_AUDIO;
            }
        } else {
            this->update_tx(); // Update sending to IC
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(1));
    }
}

void AGW::update_tx() {
    // Assume is_sending is true
    if (this->hasFC && get_millis() - lastSendMillis >= this->st_min) {
        // Tx next frame!
        this->tx_frame.data.bytes[0] = this->pci;
        memcpy(&this->tx_frame.data.bytes[1], &tempBuffer.data[this->tx_buf_pos], std::min(7, tempBuffer.len - tx_buf_pos));
        this->tx_buf_pos += 7;
        sendFrames.pushFrame(this->tx_frame);
        if (this->tx_buf_pos >= tempBuffer.len) {
            // Tx complete!
            this->hasFC = false;
            this->isSending = false;
            return;
        }
        // Increment PCI by 1.
        this->pci++;
        if (this->pci == 0x30) { // PCI overflow!
            this->pci = 0x20;
        }
        this->lastSendMillis = get_millis();
    }
}

void AGW::sendPayload(AGWPayload p) {
    if (p.len <= 7) { // Can be done in 1 frame!
        lastSendMillis = get_millis();
        isSending = true;
        memcpy(&this->tx_frame.data.bytes[1], &p.data[0], p.len);
        sendFrames.pushFrame(this->tx_frame);
        isSending = false;
    } else {
        isSending = true;
        hasFC = false;
        this->tx_frame.data.bytes[0] = 0x10;
        this->tx_frame.data.bytes[1] = p.len;
        tempBuffer = p; // Copy
        tx_buf_pos = 6;
        memcpy(&this->tx_frame.data.bytes[2], &p.data[0], 6);
        sendFrames.pushFrame(this->tx_frame);
        lastSendMillis = get_millis();
        this->pci = 0x21; // Set PCI ID
    }
}

void AGW::setCurrentPage(uint8_t page) {
    if (page == PAGE_AUDIO) {
        audio_display->setState(true);
        tel_display->setState(false);
    } else if (page == PAGE_TEL) {
        audio_display->setState(false);
        tel_display->setState(true);
    } else {
        audio_display->setState(false);
        tel_display->setState(false);
    }
}
