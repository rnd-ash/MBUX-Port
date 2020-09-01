#include "AGWDisplay.h"
#include <cstdlib>
#include <cstring>
#include <android/log.h>

AGWDisplay::AGWDisplay(const char* init_body, const char* init_header) {
    this->body = std::string(init_body);
    this->header = std::string(init_header);
}

/**
 * Package 29 is the same between pages, so we can implement it in the base class
 */
AGWPayload AGWDisplay::createPkg29() {
    uint8_t num_chars = (uint8_t)std::min(15, (int)header.size());
    AGWPayload b = {
            (uint8_t)(num_chars + 5),
            this->pageID,
            0x29,
            this->body_fmt
    };
    memcpy(&b.data[3], &this->header.c_str()[0], num_chars);
    b.data[b.len-2] = 0x00;
    emplaceChecksum(&b);
    return b;
}

void AGWDisplay::emplaceChecksum(AGWPayload *p) {
    uint8_t cs = 0xFF;
    for (int i = 0; i < p->len-1; i++) {
        cs -= (i + p->data[i]);
    }
    p->data[p->len-1] = cs; // put CS at end of payload
}

AGWPayload *AGWDisplay::getOutgoing() {
    if (outgoingPayloads.empty()) {
        return nullptr;
    } else {
        return &outgoingPayloads.front();
    }
}

void AGWDisplay::processIncommingMessage(uint8_t pkg, uint8_t argSize, uint8_t *args) {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW-DISPLAY", "Adding PKG %02X with %d args to incomming queue for Display %02X", pkg, argSize, this->pageID);
    ICData s = {pkg, argSize};
    memcpy(&s.args[0], &args[0], argSize);
    incommingPayloads.push(s);
}

void AGWDisplay::update() {
    if (!this->incommingPayloads.empty()) {
        ICData p = this->incommingPayloads.front();
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-DISPLAY", "Sending ACK For Display %02X, PKG %02X", pageID, p.pkg);
        this->outgoingPayloads.push({0x03, pageID, p.pkg, 0x06}); // ACK back to Kombi
        this->incommingPayloads.pop();
        if (init_page) {
            if (p.pkg == 0x21 && init_stage == 0x01) { init_stage++; } // Can send PKG 24!
            if (p.pkg == 0x25 && init_stage == 0x03) { init_stage++; } // Can send PKG 26!
        } else {
            if (p.pkg == 0x22) { this->init_sequence(); } // Requesting init
        }
    }
    if (this->init_page) {
        this->init_sequence();
    }
    this->custom_update(); // Internal updating
}

void AGWDisplay::popSendQueue() {
    this->outgoingPayloads.pop();
}

void AGWDisplay::processResponse(uint8_t pkg, uint8_t resp) {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW-Display", "IC ACK for Display %02X, PKG %02X = %02X", this->pageID, pkg, resp);
    if (init_page && pkg == 0x26) {
        this->init_page = false; // Init complete!
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-Display", "INIT COMPLETE Display %02X", this->pageID);
    }
}

void AGWDisplay::start_init() {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW-Display", "Will init page %02X!", this->pageID);
    this->init_page = true;
    this->init_stage = 0x00;
    while(!this->incommingPayloads.empty()) {
        this->incommingPayloads.pop();
    }
    while(!this->outgoingPayloads.empty()) {
        this->outgoingPayloads.pop();
    }
}

void AGWDisplay::init_sequence() {
    switch (this->init_stage) {
        case 0x00:
            this->outgoingPayloads.push(createPkg20());
            this->init_stage++;
            break;
        case 0x02:
            this->outgoingPayloads.push(createPkg24());
            this->init_stage++;
            break;
        case 0x04:
            this->outgoingPayloads.push(createPkg26());
            break;
    }
}

AGWPayload AGWDisplay::createPkg20() {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW-Display", "Creating PKG 20 for Display %02X", this->pageID);
    AGWPayload p = {0x05, pageID, 0x20, 0x02, 0x11};
    p.data[4] = 254 - 0x05;
    for (int i = 0; i < p.len-1; i++) {
        p.data[4] -= p.data[i];
    }
    return p;
}

TelephoneDisplay::TelephoneDisplay(const char *header) : AGWDisplay("", header) {
    this->pageID = PAGE_TEL;
}

AGWPayload TelephoneDisplay::createPkg26() {
    return AGWPayload();
}

AGWPayload TelephoneDisplay::createPkg24() {
    return AGWPayload();
}

void TelephoneDisplay::custom_update() {
    // TODO
}

AudioDisplay::AudioDisplay(const char *body, const char *header) : AGWDisplay(body, header) {
    this->pageID = PAGE_AUDIO;
}

AGWPayload AudioDisplay::createPkg24() {
    uint8_t num_chars = (uint8_t)std::min(15, (int)header.size());
    AGWPayload b = {
            (uint8_t) (num_chars + 19),
            PAGE_AUDIO,
            0x24, // PKG 24
            0x02, // ??
            0x60, // ??
            0x00, // ??
            0x01, // ??
            0x00, // ??
            0x00, // ??
            0x00, // ??
            0x13, // ??
            symbolUpper,  // Upper symbol
            0x01, // ^^
            symbolLower,  // Lower symbol
            0x02, // ^^
            0x00, // Null terminator
            num_chars,    // Character count
            header_fmt    // Header format
    };
    memcpy(&b.data[17], &header.c_str()[0], num_chars);
    b.data[b.len-2] = 0x00; // Null terminator
    emplaceChecksum(&b);
    return b;
}

AGWPayload AudioDisplay::createPkg26() {
    uint8_t num_chars = (uint8_t)std::min(15, (int)body.size());
    AGWPayload b = {
            (uint8_t) (num_chars + 9),
            PAGE_AUDIO,
            0x26,
            0x01,
            0x00,
            0x01,
            (uint8_t)(num_chars+2),
            body_fmt
    };
    memcpy(&b.data[7], &body.c_str()[0], num_chars);
    b.data[b.len-2] = 0x00;
    emplaceChecksum(&b);
    return b;
}

void AudioDisplay::custom_update() {
    // TODO
}
