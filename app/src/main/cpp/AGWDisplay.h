//
// Created by ashcon on 8/27/20.
//

#ifndef MERCEDES_UI_AGW_DISPLAY_H
#define MERCEDES_UI_AGW_DISPLAY_H

#include <string>
#include <queue>

#define PAGE_AUDIO 0x03
#define PAGE_TEL 0x05

#define TEXT_SCROLL_MS 200
#define DISPLAY_WIDTH_PX 56
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

struct AGWPayload {
    uint8_t len;
    uint8_t data[254];
};

struct ICData {
    uint8_t pkg;
    uint8_t len;
    uint8_t args[8];
};

unsigned long get_millis();

class AGWDisplay {
public:
    AGWDisplay(const char* init_body, const char* init_header);
    std::string body;
    uint8_t body_fmt = 0x00;
    std::string header;
    uint8_t header_fmt = 0x00;
    uint8_t symbolUpper = 0x00;
    uint8_t symbolLower = 0x00;
    AGWPayload createPkg29();
    virtual AGWPayload createPkg26() = 0;
    virtual AGWPayload createPkg24() = 0;
    virtual AGWPayload createPkg28() = 0;
    AGWPayload createPkg20();
    void start_init(bool ack22);
    void update();
    void processResponse(uint8_t pkg, uint8_t resp);
    void processIncommingMessage(uint8_t pkg, uint8_t argSize, uint8_t* args);
    void popSendQueue();
    AGWPayload* getOutgoing();
    void setHeader(std::string txt, uint8_t fmt);
    void setState(bool s);
protected:
    uint8_t pkg25Buf[3]={0x00};
    void init_sequence();
    bool init_page = false;
    uint8_t init_stage = 0x00;
    virtual void custom_update() = 0;
    std::queue<AGWPayload> outgoingPayloads; // Going to Kombi
    uint8_t pageID;
    void emplaceChecksum(AGWPayload* p);
    std::queue<ICData> incommingPayloads;
    bool update_header = false;
    bool update_body = false;
    bool update_symbols = false;
    bool isActive = false;
    unsigned long lastInitTime = get_millis();
};


class TelephoneDisplay : public AGWDisplay {
public:
    TelephoneDisplay(const char* header);
    AGWPayload createPkg26();
    AGWPayload createPkg24();
    AGWPayload createPkg28();
    void setBody(std::string txt1, uint8_t fmt1, std::string txt2, uint8_t fmt2, std::string txt3, uint8_t fmt3, std::string txt4, uint8_t fmt4);
    void setSymbols(uint8_t s1, uint8_t s2, uint8_t s3, uint8_t s4);
protected:
    void custom_update();
private:
    std::string line1;
    uint8_t line1_fmt = 0x00;
    std::string line2;
    uint8_t line2_fmt = 0x00;
    std::string line3;
    uint8_t line3_fmt = 0x00;
    std::string line4;
    uint8_t line4_fmt = 0x00;
    uint8_t s1 = 0x00;
    uint8_t s2 = 0x00;
    uint8_t s3 = 0x00;
    uint8_t s4 = 0x00;
};

class AudioDisplay : public AGWDisplay {
public:
    AudioDisplay(const char* body, const char* header);
    AGWPayload createPkg26();
    AGWPayload createPkg24();
    AGWPayload createPkg28();
    void setBody(std::string txt, uint8_t fmt);
    void setSymbols(uint8_t upper, uint8_t lower);
protected:
    void custom_update();
private:
    uint8_t body_fmt_orig = 0x00;
    std::string txt_orig = "";
    bool canFitLineBody(const std::string txt);
    bool textScroll = false;
    unsigned long lastUpdateMillis = get_millis();
};

#endif //MERCEDES_UI_AGW_H
