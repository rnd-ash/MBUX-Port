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
    __android_log_print(ANDROID_LOG_DEBUG, "CanbusDecoer", "CAN FRAME: Bus: %c, ID: %04X, DLC: %d. Data: [%s]", frame->busID, frame->id, frame->dlc, buf);
    switch (frame->busID) {
        case CANB:
            busB.processFrame(frame);
            break;
        case CANC:
            busC.processFrame(frame);
            break;
        default:
            break;
    }
}

int CanbusDecoder::getValue(char bus, int ecuAddr, int offset, int len) {
    switch (bus) {
        case CANB:
            return this->busB.getValue(ecuAddr, offset, len);
        default:
            throw InvalidBusException(bus);
    }
}

void CanB::processFrame(CanFrame *frame) {
    switch (frame->id) {
        case B_KLA_A1:
            __android_log_print(ANDROID_LOG_DEBUG, "CANB" ,"Found KLAA1");
            this->KLA_A1.setData(frame);
        default:
            break;
    }
}

int CanB::getValue(int ecuAddr, int offset, int len) {
    switch (ecuAddr) {
        case B_KLA_A1:
            return KLA_A1.getParam(offset, len);
        default:
            throw InvalidECUAddressException(ecuAddr);
    }
}

void CanC::processFrame(CanFrame *frame) {
    // TODO process ECUs
}
