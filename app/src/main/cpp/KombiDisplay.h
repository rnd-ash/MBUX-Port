//
// Created by ashcon on 8/28/20.
//

#ifndef MERCEDES_UI_KOMBIDISPLAY_H
#define MERCEDES_UI_KOMBIDISPLAY_H

#include <cstdlib>
#include <stdint.h>
#include <string>
#include <sys/time.h>

#define DISPLAY_AUDIO 0x03
#define DISPLAY_TEL 0x05

unsigned long get_millis();

#define DISPLAY_WIDTH_PX 56

struct isoBuffer {
    uint8_t* ptr;
    uint8_t len;
};

class abstractPage {
public:
    virtual void update() = 0;
    virtual isoBuffer generatePackage29() = 0; // Header text
    virtual isoBuffer generatePackage26() = 0; // Body text
    virtual isoBuffer generatePackage24() = 0; // Init package
    bool update_body = false;
    bool update_header = false;
    bool update_symbols = false;
protected:
    int scroll_freq_body_ms = 150;
    bool isScrollingBody = false;
    uint8_t* workBuffer = nullptr;
    uint8_t workBufferLen;
    void emplaceCS();
    bool canFitLineBody(const std::string txt);
    bool canFitLineHeader(const std::string txt);
};

class TelPage : public abstractPage {
public:
    void update();
    isoBuffer generatePackage29();
    isoBuffer generatePackage26();
    isoBuffer generatePackage24();
private:
    std::string body_text_line1 = "----";
    std::string body_text_line2 = "----";
    std::string header_text = "----";
};

class AudioPage : public abstractPage {
public:
    void update();
    void setHeader(std::string txt, uint8_t justification);
    void setBody(std::string txt, uint8_t justification);
    isoBuffer generatePackage29();
    isoBuffer generatePackage26();
    isoBuffer generatePackage24();
    uint8_t symbolUpper = 0x00;
    uint8_t symbolLower = 0x00;
    uint8_t header_fmt = 0x00;
    uint8_t body_fmt = 0x00;
private:
    unsigned long lastUpdateBodyMillis = 0;
    std::string body_text = "----";
    std::string body_text_orig = "----";
    std::string header_text = "----";
    std::string header_text_orig = "----";
};

#endif //MERCEDES_UI_KOMBIDISPLAY_H
