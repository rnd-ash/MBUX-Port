 
#include <Arduino.h>
#include "mcp2515.h"


#define RGB // Uncomment if your setup does not include RGB

#ifdef RGB
#include "rgb.h"
#endif


// Change these 2 defines if your CS pins are different
// They CANNOT be the same
#define CANB_CS 4
#define CANC_CS 5

MCP2515* canC;
MCP2515* canB;

RGB_Manager *rgb;

// IO struct with the tablet
// It is a compressed CAN Frame
struct tablet_frame {
    char can_bus_id;
    uint16_t id; // First 2 bytes for these cars are always 0000, so ignore
    uint8_t dlc;
    uint8_t data[8];
};

const uint8_t FRAME_SIZE = sizeof(tablet_frame);

tablet_frame io_frame = {0x00}; // Reserve in memory
can_frame io_can_frame = {0x00}; // Reserve this in memory as well

void setup() {
    Serial.begin(115200);

    // Init the CAN modules
    canB = new MCP2515(CANB_CS);
    canC = new MCP2515(CANC_CS);
    canB->reset();
    canC->reset();
    canB->setBitrate(CAN_83K3BPS);
    canC->setBitrate(CAN_500KBPS);
    // I trust myself to write to CAN B
    // Set it as Read + Write
    canB->setNormalMode();

    // I don't trust myself to write to CAN C
    // Set it as read only!
    canC->setListenOnlyMode(); 


    // CAN B - Listen to this data:


    // Setup the RGB!
#ifdef RGB
    rgb = new RGB_Manager();
#endif

    // TODO - Filtering - We don't need EVERY message on either bus!
}

char writeBuf[30]={0x00};
void writeFrame(char bus_id, can_frame* f) {
    uint8_t pos = sprintf(writeBuf, "%c%04X", bus_id, f->can_id);
    for (int i = 0; i < f->can_dlc; i++) {
        pos+=sprintf(writeBuf+pos, "%02X", f->data[i]);
    }
    writeBuf[pos] = '\n';
    Serial.write(writeBuf, pos+1);
}

uint32_t last_sent_b = 0xFFFF;
uint32_t last_sent_c = 0xFFFF;
bool w = false;

unsigned long last_update_millis = millis();
void loop() {
    // Incomming request from tablet! - Send to CAN
    if (Serial.available() >= FRAME_SIZE) {
        Serial.readBytes((char*)&io_frame, FRAME_SIZE);
        io_can_frame.can_id = io_frame.id;
        io_can_frame.can_dlc = io_frame.dlc;
        memcpy(io_can_frame.data, io_frame.data, io_frame.dlc);
        switch (io_frame.can_bus_id)
        {

#ifdef RGB
        case 'D':
            // 'D' frames are special as they are commands for the RGB Subsystem
            // Byte 0 - Channel ID 
            // Byte 1 - step count for fade (Each step is 10ms)
            // Byte 2 - R
            // Byte 3 - G
            // Byte 4 - B
            // Byte 5 - Boolean indicating if we should save the colour of the zone to EEPROM
            if (io_can_frame.can_dlc == 6) {
                rgb->write_channel(io_can_frame.data[0], io_can_frame.data[1], io_can_frame.data[2], io_can_frame.data[3], io_can_frame.data[4], io_can_frame.data[5]);
            }
            break;
#endif
        // DO NOT SEND C DATA
        //case 'C':
        //    canC->sendMessage(&io_can_frame);
        //    break;
        case 'B':
            canB->sendMessage(&io_can_frame);
            break;
        default:
            break;
        }
    }
    // Poll for any new CAN frames on Bus B
    if (canB->readMessage(&io_can_frame) == MCP2515::ERROR_OK) {
        // Don't send AGW_KOMBI, SAM_H_A2, SAM_H_A4 back to headunit as headunit sends those frames
        if (io_can_frame.can_id != 0x01A4 && io_can_frame.can_id != 0x000E && io_can_frame.can_id != 0x0230) {
            writeFrame('B', &io_can_frame);
        }
    }
    // Poll for any new CAN frames on Bus C
    if (canC->readMessage(&io_can_frame) == MCP2515::ERROR_OK) {
        writeFrame('C', &io_can_frame);
    }
    if (millis() >= last_update_millis + 10) {
        last_update_millis = millis();
        rgb->update();
    }
}