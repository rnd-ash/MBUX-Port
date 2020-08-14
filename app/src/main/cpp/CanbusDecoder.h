//
// Created by ashcon on 8/13/20.
//

#ifndef MERCEDES_UI_CANBUSDECODER_H
#define MERCEDES_UI_CANBUSDECODER_H

#include "ECUs/ECU.h"
#include <map>

class InvalidBusException : public std::exception {
public:
    InvalidBusException(char bus) {
        this->bus = bus;
    }
    std::string what() {
        char buf[50];
        sprintf(buf, "Invalid bus specifier '%c'", this->bus);
        return std::string(buf);
    }
private:
    char bus;
};

class InvalidECUAddressException : public std::exception {
public:
    InvalidECUAddressException(int addr) {
        this->addr = addr;
    }
    std::string what() {
        char buf[50];
        sprintf(buf, "Unknown ECU Address '0x%04X'", this->addr);
        return std::string(buf);
    }
private:
    char addr;
};

class CanDB {
public:
    void processFrame(CanFrame* frame);
    int getValue(int ecuAddr, int offset, int len);
    CanFrame* getFrame(uint16_t ecuAddr);
private:
    std::map<uint16_t, ECUFrame> frames;
};

class CanbusDecoder {
public:
    void processFrame(CanFrame* frame);
    CanDB canB;
    CanDB canC;
    int getValue(char bus, int ecuAddr, int offset, int len);
    CanFrame* getFrame(char bus, int ecuAddr);
};


#endif //MERCEDES_UI_CANBUSDECODER_H
