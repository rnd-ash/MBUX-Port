//
// Created by ashcon on 8/28/20.
//

#include "KombiDisplay.h"

const uint8_t CHAR_WIDTHS_BODY[256] = {   // Global variable so PROGMEM attribute can be used
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 7, 6, 0, 0, 0,
        0, 6, 6, 6, 7, 7, 3, 2,
        7, 7, 0, 0,10,10, 6, 6,
        6, 3, 4, 6, 6, 6, 6, 2,
        5, 5, 6, 6, 3, 5, 2, 6,
        7, 7, 7, 7, 7, 7, 7, 7,
        7, 7, 3, 4, 5, 6, 5, 6,

        6, 7, 7, 7, 7, 6, 6, 7,
        7, 3, 5, 7, 6, 7, 0, 0,
        7, 7, 7, 7, 7, 7, 7,11,
        7, 7, 7, 4, 6, 4, 3, 6,
        3, 6, 6, 6, 6, 7, 6, 8,
        6, 3, 5, 6, 3, 9, 7, 7,
        6, 6, 6, 6, 5, 7, 7, 9,
        7, 6, 6, 6, 2, 6,99, 0, // One marked with 99 crashes the IC!

        7, 6, 8, 9, 6, 6, 6, 6,
        7, 6, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,

        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0
};

unsigned long get_millis() {
    struct timeval detail_time{};
    gettimeofday(&detail_time,NULL);
    return (detail_time.tv_usec / 1000); // Milliseconds
}


// -- BASE CLASS --

// Emplaces the checksum of the buffer at the last position in the buffer
void abstractPage::emplaceCS() {
    uint8_t cs = 0xFF;
    for (int i = 0; i < workBufferLen-1; i++) {
        cs -= i;
        cs -= workBuffer[i];
    }
    workBuffer[workBufferLen-1] = cs;
}

bool abstractPage::canFitLineBody(const std::string txt) {
    int total_length = 0;
    for (char i : txt) { total_length += CHAR_WIDTHS_BODY[(uint8_t)i]; }
    return total_length <= DISPLAY_WIDTH_PX;
}

// TELEPHONE PAGE METHODS

isoBuffer TelPage::generatePackage29() {
    return isoBuffer{ workBuffer, workBufferLen };
}

isoBuffer TelPage::generatePackage26() {
    return isoBuffer{ workBuffer, workBufferLen };
}

isoBuffer TelPage::generatePackage24() {
    return isoBuffer{ workBuffer, workBufferLen };
}

void TelPage::update() {

}


// AUDIO PAGE METHODS
isoBuffer AudioPage::generatePackage29() {
    delete[] workBuffer;
    int str_chars = std::min(12, (int)header_text.length());
    workBufferLen = str_chars + 5;
    workBuffer = new uint8_t[workBufferLen];
    workBuffer[0] = 0x03;
    workBuffer[1] = 0x29;
    workBuffer[2] = 0x00;
    char* c_str = (char*)header_text.c_str();
    memcpy(&workBuffer[3], &c_str[0], str_chars);
    workBuffer[workBufferLen-2] = 0x00;
    emplaceCS();
    return isoBuffer{ workBuffer, workBufferLen };
}

isoBuffer AudioPage::generatePackage26() {
    delete[] workBuffer;
    int str_chars = std::min(12, (int)body_text.length());
    workBufferLen = str_chars + 9;
    workBuffer = new uint8_t[workBufferLen];
    workBuffer[0] = DISPLAY_AUDIO;
    workBuffer[1] = 0x26;
    workBuffer[2] = 0x01;
    workBuffer[3] = 0x00;
    workBuffer[4] = 0x01;
    workBuffer[5] = 2 + str_chars;
    workBuffer[6] = body_fmt;
    char* c_str = (char*)body_text.c_str();
    memcpy(&workBuffer[7], &c_str[0], str_chars);
    workBuffer[workBufferLen-2] = 0x00;
    emplaceCS();
    return isoBuffer{ workBuffer, workBufferLen };
}

isoBuffer AudioPage::generatePackage24() {
    delete[] workBuffer;
    int str_chars = std::min(12, (int)header_text.length());
    workBufferLen = str_chars + 19;
    workBuffer = new uint8_t[workBufferLen];
    workBuffer[0] = DISPLAY_AUDIO;
    workBuffer[1] = 0x24;
    workBuffer[2] = 0x02;
    workBuffer[3] = 0x60;
    workBuffer[4] = 0x00;
    workBuffer[5] = 0x01;
    workBuffer[6] = 0x00;
    workBuffer[7] = 0x00;
    workBuffer[8] = 0x00;
    workBuffer[9] = 0x13;
    workBuffer[10] = symbolUpper;
    workBuffer[11] = 0x01;
    workBuffer[12] = symbolLower;
    workBuffer[13] = 0x02;
    workBuffer[14] = header_fmt;
    workBuffer[15] = str_chars + 2;
    workBuffer[16] = 0x10; // FMT
    char* c_str = (char*)header_text.c_str();
    memcpy(&workBuffer[17], &c_str[0], str_chars);
    workBuffer[workBufferLen-2] = 0x00;
    emplaceCS();
    return isoBuffer{ workBuffer, workBufferLen };
}

void AudioPage::update() {
    if (isScrollingBody) {
        if (get_millis() - lastUpdateBodyMillis >= scroll_freq_body_ms) {
            char tmp = this->body_text[0];
            for (int i = 1; i < this->body_text.length(); i++) {
                this->body_text[i-1] = this->body_text[i];
            }
            this->body_text[this->body_text.length()-1] = tmp;
            this->update_body = true;
            this->lastUpdateBodyMillis = get_millis();
        }
    }
}

void AudioPage::setBody(std::string txt, uint8_t justification) {
    if (this->body_fmt != justification) {
        this->body_fmt = justification;
        this->update_body = true;
    }
    if (txt != this->body_text_orig) {
        this->isScrollingBody = false;
        this->body_text_orig = txt;
        this->body_text = txt;
        if (!canFitLineBody(txt)) {
            this->body_text += "   ";
            this->isScrollingBody = true;
        }
        this->update_body = true;
        lastUpdateBodyMillis = get_millis();
    }
    // If scrolling is required, force body format to right justification
    if (!canFitLineBody(txt)) {
        this->body_fmt = 0x00;
    }
}

void AudioPage::setHeader(std::string txt, uint8_t justification) {
    if (this->header_fmt != justification) {
        this->header_fmt = justification;
        this->update_header = true;
    }
    if (txt != this->header_text_orig) {
        this->header_text_orig = txt;
        this->header_text = txt;
        this->update_header = true;
    }
}
