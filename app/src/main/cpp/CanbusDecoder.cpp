//
// Created by ashcon on 8/13/20.
//

#include <string>
#include "CanbusDecoder.h"


void CanbusDecoder::processFrame(CanFrame *frame) {
    switch (frame->busID) {
        case CANB:
            if (frame->id == 0x01D0) {
                this->agw->processKombiFrame(frame);
            }
            canB.processFrame(frame);
            break;
        case CANC:
            canC.processFrame(frame);
            break;
        default:
            break;
    }
}

int CanbusDecoder::getValue(char bus, int ecuAddr, int offset, int len) {
    switch (bus) {
        case CANB:
            return this->canB.getValue(ecuAddr, offset, len);
        case CANC:
            return this->canC.getValue(ecuAddr, offset, len);
        default:
            throw InvalidBusException(bus);
    }
}

CanFrame *CanbusDecoder::getFrame(char bus, int ecuAddr) {
    switch (bus) {
        case CANB:
            return this->canB.getFrame(ecuAddr);
        case CANC:
            return this->canC.getFrame(ecuAddr);
        default:
            throw InvalidBusException(bus);
    }
}

CanbusDecoder::CanbusDecoder() {
    this->agw = new AGW();
}

void CanDB::processFrame(CanFrame *frame) {
    if (frames.find(frame->id) == frames.end()) {
        // Insert ECU Frame if not existing
        this->frames.insert(std::make_pair(frame->id, ECUFrame()));
    }
    // Update Frame
    frames[frame->id].setData(frame);
}

int CanDB::getValue(int ecuAddr, int offset, int len) {
    try {
        return frames[ecuAddr].getParam(offset, len);
    } catch (...) {
        throw InvalidECUAddressException(ecuAddr);
    }
}

CanFrame *CanDB::getFrame(uint16_t ecuAddr) {
    try {
        return frames[ecuAddr].getData();
    } catch (...) {
        return nullptr;
    }
}
