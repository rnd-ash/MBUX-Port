//
// Created by ashcon on 8/13/20.
//

#include <string>
#include "CanbusDecoder.h"

void CanbusDecoder::processFrame(CanFrame *frame) {
    int pos = 0;
    char buf[25] = {0x00};
    for (int i = 0; i < frame->dlc; i++) {
        pos += sprintf(buf+pos, "%02X ", frame->data[i]);
    }
    //__android_log_print(ANDROID_LOG_DEBUG, "CanbusDecoer", "CAN FRAME: Bus: %c, ID: %04X, DLC: %d. Data: [%s]", frame->busID, frame->id, frame->dlc, buf);
    switch (frame->busID) {
        case CANB:
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
