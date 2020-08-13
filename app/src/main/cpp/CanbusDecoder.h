//
// Created by ashcon on 8/13/20.
//

#ifndef MERCEDES_UI_CANBUSDECODER_H
#define MERCEDES_UI_CANBUSDECODER_H

#include "ECUs/ECU.h"
#include "ECUs/Addrs.h"

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

class InvalidECUAddressException : public std::exception  {
public:
    InvalidECUAddressException(int id) {
        this->id = id;
    }
    std::string what() {
        char buf[50];
        sprintf(buf, "Unknown ECU ID '0x%04X'", this->id);
        return std::string(buf);
    }
private:
    int id;
};



class CanB {
public:
    void processFrame(CanFrame* frame);
    int getValue(int ecuAddr, int offset, int len);
private:
    ECUFrame KLA_A1 = ECUFrame();
};

class CanC {
public:
    void processFrame(CanFrame* frame);
private:
    // TODO Add ECUs
};

class CanbusDecoder {
public:
    void processFrame(CanFrame* frame);
    CanB busB = CanB();
    CanC busC = CanC();
    int getValue(char bus, int ecuAddr, int offset, int len);
};


#endif //MERCEDES_UI_CANBUSDECODER_H
