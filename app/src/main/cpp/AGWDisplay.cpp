#include "AGWDisplay.h"
#include <cstdlib>
#include <cstring>
#include <android/log.h>

unsigned long get_millis() {
    struct timeval detail_time{};
    gettimeofday(&detail_time,NULL);
    return (detail_time.tv_usec / 1000);
}

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
        if (p.pkg != 0x22) {
            __android_log_print(ANDROID_LOG_DEBUG, "AGW-DISPLAY", "Sending ACK For Display %02X, PKG %02X", pageID, p.pkg);
            if (p.pkg == 0x25) {
                memcpy(&pkg25Buf[0], &p.args[0], 3);
            } else if (p.pkg == 0x27) {
                if (p.args[0] == 0x06) {
                    this->isActive = true;
                } else {
                    this->isActive = false;
                }
            }
            this->outgoingPayloads.push({0x03, pageID, p.pkg, 0x06}); // ACK back to Kombi
            this->incommingPayloads.pop();
            if (init_page) { // Within an init sequence, so work out what packet we need to send next
                if (p.pkg == 0x21 && init_stage == 0x01) { init_stage++; } // Can send PKG 24!
                if (p.pkg == 0x25 && init_stage == 0x03) { init_stage++; } // Can send PKG 26!
            }
        } else {
            this->start_init(true);
        }
    }
    if (this->init_page) {
        if (get_millis() - this->lastInitTime > 2000) {
            __android_log_print(ANDROID_LOG_WARN, "AGW-DISPLAY", "INIT Timeout for page %02X, trying again", this->pageID);
            this->start_init(false);
        }
        this->init_sequence();
    } else {
        if (this->isActive) { // Only do custom updating if in page!
            this->custom_update(); // Internal page updating
        }
    }
}

void AGWDisplay::popSendQueue() {
    this->outgoingPayloads.pop();
}

void AGWDisplay::processResponse(uint8_t pkg, uint8_t resp) {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW-Display", "IC ACK for Display %02X, PKG %02X = %02X", this->pageID, pkg, resp);
    if (init_page && pkg == 0x26 && init_stage == 0x05) {
        this->init_page = false; // Init complete!
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-Display", "INIT COMPLETE Display %02X", this->pageID);
    }
}

void AGWDisplay::setHeader(std::string txt, uint8_t fmt) {
    if (txt != this->header || fmt != this->header_fmt) {
        this->header = txt;
        this->header_fmt = fmt;
        this->update_header = true;
    }
}

void AGWDisplay::start_init(bool ack22) {
    __android_log_print(ANDROID_LOG_DEBUG, "AGW-Display", "Will init page %02X!", this->pageID);
    this->init_page = true;
    this->init_stage = 0x00;
    while(!this->incommingPayloads.empty()) {
        this->incommingPayloads.pop();
    }
    while(!this->outgoingPayloads.empty()) {
        this->outgoingPayloads.pop();
    }
    if (ack22) {
        this->outgoingPayloads.push({0x03, pageID, 0x22, 0x06});
    }
    this->lastInitTime = get_millis();
}

void AGWDisplay::init_sequence() {
    switch (this->init_stage) {
        case 0x00: // Start of Init - send PKG 20
            this->outgoingPayloads.push(createPkg20());
            this->init_stage++;
            break;
        case 0x02: // IC has sent PKG 21 - Send PKG 24
            this->outgoingPayloads.push(createPkg24());
            this->init_stage++;
            break;
        case 0x04: // IC has send PKG 25 - Send PKG 25
            this->outgoingPayloads.push(createPkg26());
            this->init_stage++;
            break;
        default:
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

void AGWDisplay::setState(bool s) {
    if (s) {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-DISPLAY", "Page %02X is now active", this->pageID);
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-DISPLAY", "Page %02X is now inactive", this->pageID);
    }
    this->isActive = s;
}

TelephoneDisplay::TelephoneDisplay(const char *header) : AGWDisplay("", header) {
    this->pageID = PAGE_TEL;
    this->line1 = "Line1";
    this->line2 = "Line2";
    this->line3 = "Line3";
    this->line4 = "Line4";
    this->header = "TEL";
}

AGWPayload TelephoneDisplay::createPkg26() {
    uint8_t num_chars1 = (uint8_t)std::min(30, (int)line1.size());
    uint8_t num_chars2 = (uint8_t)std::min(30, (int)line2.size());
    uint8_t num_chars3 = (uint8_t)std::min(30, (int)line3.size());
    uint8_t num_chars4 = (uint8_t)std::min(30, (int)line4.size());
    uint8_t total_chars = num_chars1 + num_chars2 + num_chars3 + num_chars4;
    AGWPayload b = {
            (uint8_t)(total_chars + 15),
            PAGE_TEL,
            0x26,
            0x01,
            0x00,
            0x04,
    };
    uint8_t index = 5;

    // Line 1
    b.data[index] = num_chars1 + 2;
    b.data[index+1] = line1_fmt;
    memcpy(&b.data[index+2], &line1.c_str()[0], num_chars1);
    index += num_chars1+2;

    // Line 2
    b.data[index] = num_chars2 + 2;
    b.data[index+1] = line2_fmt;
    memcpy(&b.data[index+2], &line2.c_str()[0], num_chars2);
    index += num_chars2+2;

    // Line 3
    b.data[index] = num_chars2 + 2;
    b.data[index+1] = line3_fmt;
    memcpy(&b.data[index+2], &line3.c_str()[0], num_chars3);
    index += num_chars3+2;

    // Line 4
    b.data[index] = num_chars2 + 2;
    b.data[index+1] = line4_fmt;
    memcpy(&b.data[index+2], &line4.c_str()[0], num_chars4);
    index += num_chars4+2;

    b.data[index] = 0x00;
    emplaceChecksum(&b);
    return b;
}

AGWPayload TelephoneDisplay::createPkg24() {
    uint8_t num_chars = (uint8_t)std::min(15, (int)header.size());
    AGWPayload b = {
            (uint8_t) (num_chars + 23),
            PAGE_TEL,
            0x24, // PKG 24
            0x02,
            0x60, // ??
            0x00, // ??
            0x04, // Number lines
            0x00, // ??
            0x00, // ??
            0x00, // ??
            0x15,
            s1,  // Upper symbol
            0x01, // ^^
            s2,  // Lower symbol
            0x02, // ^^
            s3,  // Lower symbol
            0x03, // ^^
            s4,  // Lower symbol
            0x04, // ^^
            0x00, // Null terminator
            num_chars,  // Character count
            header_fmt    // Header format
    };
    memcpy(&b.data[21], &header.c_str()[0], num_chars);
    b.data[b.len-2] = 0x00; // Null terminator
    emplaceChecksum(&b);
    return b;
}

AGWPayload TelephoneDisplay::createPkg28() {
    AGWPayload b = {
            12,
            PAGE_TEL,
            0x28,
            0x04, // Number of symbols
            s1,
            0x01,
            s2,
            0x02,
            s3,
            0x03,
            s4,
            0x04
    };
    emplaceChecksum(&b);
    return b;
}

void TelephoneDisplay::custom_update() {
    if (this->update_header) {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-TEL", "Need to update header");
        this->outgoingPayloads.push(createPkg29());
        this->update_header = false;
        return;
    }
    if (this->update_symbols) {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-TEL", "Need to update symbols");
        this->outgoingPayloads.push(this->createPkg28());
        this->update_symbols = false;
        return;
    }
    if (update_body) {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-TEL", "Need to update body");
        this->outgoingPayloads.push(createPkg26());
        this->update_body = false;
        return;
    }
}

void TelephoneDisplay::setBody(std::string txt1, uint8_t fmt1, std::string txt2, uint8_t fmt2,
                               std::string txt3, uint8_t fmt3, std::string txt4, uint8_t fmt4) {
    this->line1 = txt1;
    this->line1_fmt = fmt1;
    this->line2 = txt2;
    this->line2_fmt = fmt2;
    this->line3 = txt3;
    this->line3_fmt = fmt3;
    this->line4 = txt4;
    this->line4_fmt = fmt4;
    this->update_body = true;
}

void TelephoneDisplay::setSymbols(uint8_t s1, uint8_t s2, uint8_t s3, uint8_t s4) {
    this->s1 = s1;
    this->s2 = s2;
    this->s3 = s3;
    this->s4 = s4;
    this->update_symbols = true;
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
            0x02,
            0x60, // ??
            0x00, // ??
            0x01, // Number lines
            0x00, // ??
            0x00, // ??
            0x00, // ??
            0x13,
            symbolUpper,  // Upper symbol
            0x01, // ^^
            symbolLower,  // Lower symbol
            0x02, // ^^
            0x00, // Null terminator
            num_chars,  // Character count
            header_fmt    // Header format
    };
    memcpy(&b.data[17], &header.c_str()[0], num_chars);
    b.data[b.len-2] = 0x00; // Null terminator
    emplaceChecksum(&b);
    return b;
}

AGWPayload AudioDisplay::createPkg26() {
    uint8_t num_chars = (uint8_t)std::min(30, (int)body.size());
    AGWPayload b = {
            (uint8_t) (num_chars + 9),
            PAGE_AUDIO,
            0x26
    };
    // data[2] - data[4] is copied from PKG25 that the IC sent us
    memcpy(&b.data[2], &pkg25Buf[0], 3);
    // Number of chars +2? (Setting to number of chars will concat the string by 2)
    b.data[5] = (uint8_t)(num_chars+2);
    b.data[6] = body_fmt;
    // Copy our string to display
    memcpy(&b.data[7], &body.c_str()[0], num_chars);
    // Null terminator
    b.data[b.len-2] = 0x01;
    emplaceChecksum(&b);
    return b;
}

void AudioDisplay::custom_update() {
    if (this->update_header) {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-AUDIO", "Need to update header");
        this->outgoingPayloads.push(createPkg29());
        this->update_header = false;
        return;
    }
    if (this->update_symbols) {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-AUDIO", "Need to update symbols");
        this->outgoingPayloads.push(this->createPkg28());
        this->update_symbols = false;
        return;
    }
    if (update_body) {
        __android_log_print(ANDROID_LOG_DEBUG, "AGW-AUDIO", "Need to update body");
        this->outgoingPayloads.push(createPkg26());
        this->update_body = false;
        this->lastUpdateMillis = get_millis()+2000; // This way text stays on the screen for a bit before scrolling
        return;
    }
    if (textScroll && get_millis() - lastUpdateMillis >= TEXT_SCROLL_MS) {
        this->lastUpdateMillis = get_millis();
        this->update_body = false;
        char tmp = this->body[0];
        for (int i = 1; i < this->body.size(); i++) {
            this->body[i-1] = this->body[i];
        }
        this->body[this->body.size()-1] = tmp;
        this->outgoingPayloads.push(createPkg26());
    }
}

bool AudioDisplay::canFitLineBody(const std::string txt) {
    int total_length = 0;
    for (char i : txt) { total_length += CHAR_WIDTHS_BODY[(uint8_t)i]; }
    return total_length <= DISPLAY_WIDTH_PX;
}

void AudioDisplay::setBody(std::string txt, uint8_t fmt) {
    if (txt != this->txt_orig || this->body_fmt != this->body_fmt_orig) {
        this->txt_orig = txt;
        this->body_fmt_orig = fmt;
        this->body_fmt = fmt;
        this->body = txt;
        this->textScroll = !canFitLineBody(txt);
        this->lastUpdateMillis = 0; // Update now!
        if (this->textScroll) {
            this->body += "   ";
            this->body_fmt = 0x00; // Force right justification when scrolling
        }
        this->update_body = true;
    }
}

void AudioDisplay::setSymbols(uint8_t upper, uint8_t lower) {
    if (upper != this->symbolUpper || lower != this->symbolLower) {
        this->symbolUpper = upper;
        this->symbolLower = lower;
        this->update_symbols = true;
    }
}

AGWPayload AudioDisplay::createPkg28() {
    AGWPayload b = {
            0x08,
            PAGE_AUDIO,
            0x28,
            0x02, // Number of symbols
            symbolUpper,
            0x01, // Upper symbol
            symbolLower,
            0x02 // Lower symbol
    };
    emplaceChecksum(&b);
    return b;
}
