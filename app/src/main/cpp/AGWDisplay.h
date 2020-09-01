//
// Created by ashcon on 8/27/20.
//

#ifndef MERCEDES_UI_AGW_DISPLAY_H
#define MERCEDES_UI_AGW_DISPLAY_H

#include <string>
#include <queue>

#define PAGE_AUDIO 0x03
#define PAGE_TEL 0x05

struct AGWPayload {
    uint8_t len;
    uint8_t data[254];
};

struct ICData {
    uint8_t pkg;
    uint8_t len;
    uint8_t args[8];
};

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
    AGWPayload createPkg20();
    void start_init();
    void update();
    void processResponse(uint8_t pkg, uint8_t resp);
    void processIncommingMessage(uint8_t pkg, uint8_t argSize, uint8_t* args);
    void popSendQueue();
    AGWPayload* getOutgoing();
protected:
    void init_sequence();
    bool init_page = false;
    uint8_t init_stage = 0x00;
    uint8_t nextResponse;
    virtual void custom_update() = 0;
    std::queue<AGWPayload> outgoingPayloads; // Going to Kombi
    uint8_t pageID;
    bool init_complete = false;
    void emplaceChecksum(AGWPayload* p);
    std::queue<ICData> incommingPayloads;
};


class TelephoneDisplay : public AGWDisplay {
public:
    TelephoneDisplay(const char* header);
    AGWPayload createPkg26();
    AGWPayload createPkg24();
protected:
    void custom_update();
};

class AudioDisplay : public AGWDisplay {
public:
    AudioDisplay(const char* body, const char* header);
    AGWPayload createPkg26();
    AGWPayload createPkg24();
protected:
    void custom_update();
};

#endif //MERCEDES_UI_AGW_H
