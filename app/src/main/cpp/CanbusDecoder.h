//
// Created by ashcon on 8/13/20.
//

#ifndef MERCEDES_UI_CANBUSDECODER_H
#define MERCEDES_UI_CANBUSDECODER_H

#include "AGW.h"
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
    CanbusDecoder();
    void processFrame(CanFrame* frame);
    // Database for CAN B
    CanDB canB;

    // Database for CAN C
    CanDB canC;

    /**
     * Gets a value from within a can frame
     * @param bus Can BUS ECU Frame is from
     * @param ecuAddr ECU Frame ID
     * @param offset offset from bit 0 within the frame to read the value from
     * @param len Length in bits the value is
     * @return Extracted value
     */
    int getValue(char bus, int ecuAddr, int offset, int len);

    /**
     * Returns a can frame from a bus
     * @param bus Can Bus for frame
     * @param ecuAddr ECU Frame ID
     * @return Nullptr if frame not found, else pointer to can frame
     */
    CanFrame* getFrame(char bus, int ecuAddr);
    AGW* agw;
};


#endif //MERCEDES_UI_CANBUSDECODER_H
